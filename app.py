import os
from datetime import date, datetime
from dotenv import load_dotenv
from flask import Flask, render_template, request, redirect, url_for, session, flash
from authlib.integrations.flask_client import OAuth
from models import db, Usuario, Proveedor, Categoria, Insumo, Movimiento, Lote
from werkzeug.utils import secure_filename
import bcrypt
import boto3
import requests
import numpy as np
from sklearn.linear_model import LinearRegression

load_dotenv()

app = Flask(__name__)

app.secret_key = os.environ.get('SECRET_KEY')
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:@localhost/sigi'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False


oauth = OAuth(app)
google = oauth.register(
    name='google',
    client_id=os.environ.get('GOOGLE_CLIENT_ID'),
    client_secret=os.environ.get('GOOGLE_CLIENT_SECRET'),
    server_metadata_url='https://accounts.google.com/.well-known/openid-configuration',
    client_kwargs={
        'scope': 'openid email profile'
    }
)

db.init_app(app)

with app.app_context():
    db.create_all()

@app.route('/')
def index():
    if 'usuario' in session:
        return redirect(url_for('seleccion_seccion'))
    return redirect(url_for('login'))


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        usr = request.form.get('usuario')
        pwd = request.form.get('contrasenia')  
        
        usuario_encontrado = Usuario.query.filter_by(nombre_usuario=usr).first()
        
        if usuario_encontrado:
            hash_bd = usuario_encontrado.contrasenia.encode('utf-8')
            password_ingresada = pwd.encode('utf-8')
            
            es_valida = False
            try:
                if bcrypt.checkpw(password_ingresada, hash_bd):
                    es_valida = True
            except Exception:
                if usuario_encontrado.contrasenia == pwd:
                    es_valida = True
            
            if es_valida:
                session['usuario'] = usuario_encontrado.nombre_usuario
                session['nombre_completo'] = usuario_encontrado.nombre_completo
                session['rol'] = usuario_encontrado.rol
                return redirect(url_for('seleccion_seccion'))
                
        flash('Usuario o contraseña incorrectos', 'danger')
            
    return render_template('login.html')


@app.route('/registro', methods=['GET', 'POST'])
def registro():
    if request.method == 'POST':
        usr = request.form.get('usuario')
        pwd = request.form.get('contrasenia')  
        nombre = request.form.get('nombre_completo')
        rol = request.form.get('rol', 'OPERADOR')
        
        existente = Usuario.query.filter_by(nombre_usuario=usr).first()
        if existente:
            flash('El nombre de usuario ya está registrado', 'warning')
            return redirect(url_for('registro'))
            
        hashed_pwd = bcrypt.hashpw(pwd.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        
        nuevo_usuario = Usuario(
            nombre_usuario=usr,
            contrasenia=hashed_pwd,
            nombre_completo=nombre,
            rol=rol,
            activo=True
        )
        db.session.add(nuevo_usuario)
        db.session.commit()
        flash('Registro exitoso. ¡Iniciá sesión!', 'success')
        return redirect(url_for('login'))
        
    return render_template('registro.html')


@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))


@app.route('/seleccion-seccion')
@app.route('/seleccion_seccion')
def seleccion_seccion():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    return render_template('seleccion_seccion.html')


@app.route('/fijar-seccion/<seccion>')
def fijar_seccion(seccion):
    if 'usuario' not in session:
        return redirect(url_for('login'))
    session['seccion'] = seccion
    if session.get('rol') == 'ADMIN':
        return redirect(url_for('admin_panel'))
    else:
        return redirect(url_for('operador_panel'))


@app.route('/admin_panel')
@app.route('/admin')
def admin_panel():
    if 'usuario' not in session or session.get('rol') != 'ADMIN':
        return redirect(url_for('login'))
    
   
    feriados = obtener_proximos_feriados()
    
    return render_template('admin_panel.html', feriados=feriados)


@app.route('/operador_panel')
@app.route('/operador')
def operador_panel():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    return render_template('operador_panel.html')


@app.route('/panel')
@app.route('/volver')
@app.route('/volver-al-panel')
@app.route('/volver_al_panel')
def panel_general():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    if session.get('rol') == 'ADMIN':
        return redirect(url_for('admin_panel'))
    return redirect(url_for('operador_panel'))


def obtener_insumos_seccion():
    seccion_actual = session.get('seccion', 'Hematología')
    cat_principal = Categoria.query.filter_by(nombre=seccion_actual).first()
    
    if not cat_principal:
        return []
        
    ids_categorias = [cat_principal.id_categoria]
    subcats = Categoria.query.filter_by(id_categoria_padre=cat_principal.id_categoria).all()
    ids_categorias.extend([sub.id_categoria for sub in subcats])
    
    return Insumo.query.filter(Insumo.id_categoria.in_(ids_categorias)).all()


@app.route('/gestion-insumos', methods=['GET', 'POST'])
@app.route('/gestion_insumos', methods=['GET', 'POST'])
def gestion_insumos():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    
    lista_insumos = obtener_insumos_seccion()
    lista_proveedores = Proveedor.query.all()
    
    return render_template('gestion_insumos.html', insumos=lista_insumos, proveedores=lista_proveedores)


@app.route('/agregar-insumo', methods=['POST'])
def agregar_insumo():
    if session.get('rol') != 'ADMIN':
        return redirect(url_for('admin_panel'))
    
    try:
        seccion_actual = session.get('seccion', 'Hematología')
        cat_obj = Categoria.query.filter_by(nombre=seccion_actual).first()
        id_cat = cat_obj.id_categoria if cat_obj else None

        nuevo = Insumo(
            nombre=request.form.get('nombre'),
            descripcion=request.form.get('descripcion'),
            stock_actual=int(request.form.get('stock_actual', 0)),
            stock_minimo=int(request.form.get('stock_minimo', 0)),
            stock_deseado=int(request.form.get('stock_deseado', 0)),
            unidad_medida=request.form.get('unidad_medida', 'unidad'),
            id_categoria=id_cat
        )
        
        id_prov = request.form.get('id_proveedor')
        if id_prov:
            prov_obj = Proveedor.query.get(id_prov)
            if prov_obj:
                nuevo.proveedores.append(prov_obj)

        db.session.add(nuevo)
        db.session.commit()
        flash('Insumo agregado correctamente.', 'success')
    except Exception as e:
        db.session.rollback()
        flash(f'Error al agregar insumo: {e}', 'danger')
        
    return redirect(url_for('gestion_insumos'))


@app.route('/modificar-insumo/<int:id_insumo>', methods=['POST'])
def modificar_insumo(id_insumo):
    if session.get('rol') != 'ADMIN':
        return redirect(url_for('admin_panel'))
    
    insumo = Insumo.query.get_or_404(id_insumo)
    try:
        insumo.nombre = request.form.get('nombre')
        insumo.descripcion = request.form.get('descripcion')
        insumo.stock_actual = int(request.form.get('stock_actual', 0))
        insumo.stock_minimo = int(request.form.get('stock_minimo', 0))
        insumo.stock_deseado = int(request.form.get('stock_deseado', 0))
        insumo.unidad_medida = request.form.get('unidad_medida')
        
        id_prov = request.form.get('id_proveedor')
        insumo.proveedores.clear()
        if id_prov:
            prov_obj = Proveedor.query.get(id_prov)
            if prov_obj:
                insumo.proveedores.append(prov_obj)
        
        db.session.commit()
        flash('Insumo modificado correctamente.', 'success')
    except Exception as e:
        db.session.rollback()
        flash(f'Error al modificar insumo: {e}', 'danger')
        
    return redirect(url_for('gestion_insumos'))


@app.route('/eliminar-insumo/<int:id_insumo>', methods=['POST'])
def eliminar_insumo(id_insumo):
    if session.get('rol') != 'ADMIN':
        return redirect(url_for('admin_panel'))
    
    insumo = Insumo.query.get_or_404(id_insumo)
    try:
        db.session.delete(insumo)
        db.session.commit()
        flash('Insumo eliminado correctamente.', 'success')
    except Exception as e:
        db.session.rollback()
        flash('No se puede eliminar el insumo porque tiene movimientos o lotes asociados.', 'danger')
        
    return redirect(url_for('gestion_insumos'))

@app.route('/ver-stock')
@app.route('/ver_stock')
@app.route('/insumos')
@app.route('/stock')
def ver_stock():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    lista_insumos = obtener_insumos_seccion()
    return render_template('insumos.html', insumos=lista_insumos)

@app.route('/estadisticas', methods=['GET', 'POST'])
def estadisticas():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    
    insumos_seccion = obtener_insumos_seccion()
    total_insumos = len(insumos_seccion)
    insumos_criticos = [i for i in insumos_seccion if i.stock_actual <= i.stock_minimo]
    
    ids_insumos = [i.id_insumo for i in insumos_seccion]
    total_movimientos = Movimiento.query.filter(Movimiento.id_insumo.in_(ids_insumos)).count() if ids_insumos else 0
    
    consumos_periodo = []
    fecha_desde = request.form.get('fecha_desde')
    fecha_hasta = request.form.get('fecha_hasta')
    
    if fecha_desde and fecha_hasta:
        try:
            d_desde = datetime.strptime(fecha_desde, '%Y-%m-%d').date()
            d_hasta = datetime.strptime(fecha_hasta, '%Y-%m-%d').date()
            
            movs = Movimiento.query.filter(
                Movimiento.fecha >= d_desde,
                Movimiento.fecha <= d_hasta,
                Movimiento.tipo == 'CONSUMO',
                Movimiento.id_insumo.in_(ids_insumos)
            ).all()
            
            consumos_dict = {}
            for m in movs:
                nombre = m.insumo.nombre if m.insumo else 'Desconocido'
                consumos_dict[nombre] = consumos_dict.get(nombre, 0) + m.cantidad
            
            dias = (d_hasta - d_desde).days + 1
            if dias <= 0: dias = 1
            
            for ins, cant in consumos_dict.items():
                consumos_periodo.append({
                    'insumo': ins,
                    'cantidad': cant,
                    'promedio': round(cant / dias, 2)
                })
        except Exception as e:
            flash(f'Error en las fechas: {e}', 'danger')

    
    predicciones = []
    for insumo in insumos_seccion:
       
        movimientos = Movimiento.query.filter_by(id_insumo=insumo.id_insumo, tipo='CONSUMO').order_by(Movimiento.fecha.asc()).all()
        
        dias_estimados_agotamiento = 999 
        estado_ia = 'Estable'
        
        if len(movimientos) >= 2:
            
            fecha_base = movimientos[0].fecha
            X_dias = []
            y_acumulado = []
            total_consumido_acum = 0
            
            for m in movimientos:
                delta_dias = (m.fecha - fecha_base).days
                total_consumido_acum += m.cantidad
                X_dias.append([delta_dias])
                y_acumulado.append(total_consumido_acum)
            
            X = np.array(X_dias)
            y = np.array(y_acumulado)
            
            modelo = LinearRegression()
            modelo.fit(X, y)
            
            
            consumo_diario_estimado = modelo.coef_[0]
            
            if consumo_diario_estimado > 0 and insumo.stock_actual > 0:
                dias_estimados_agotamiento = int(insumo.stock_actual / consumo_diario_estimado)
        else:
           
            if insumo.stock_minimo > 0 and insumo.stock_actual <= insumo.stock_minimo:
                dias_estimados_agotamiento = 2 
            else:
                dias_estimados_agotamiento = 15 
                
        if dias_estimados_agotamiento <= 5:
            estado_ia = 'Crítico'
            
        predicciones.append({
            'nombre': insumo.nombre,
            'stock_actual': insumo.stock_actual,
            'stock_minimo': insumo.stock_minimo,
            'dias_restantes': max(0, dias_estimados_agotamiento),
            'estado_ia': estado_ia
        })

    return render_template(
        'estadisticas.html',
        total_insumos=total_insumos,
        insumos_criticos=insumos_criticos,
        cant_criticos=len(insumos_criticos),
        total_movimientos=total_movimientos,
        consumos_periodo=consumos_periodo,
        fecha_desde=fecha_desde,
        fecha_hasta=fecha_hasta,
        predicciones=predicciones
    )



@app.route('/gestion-usuarios')
@app.route('/gestion_usuarios')
def gestion_usuarios():
    if 'usuario' not in session or session.get('rol') != 'ADMIN':
        return redirect(url_for('login'))
    lista_usuarios = Usuario.query.all()
    return render_template('gestion_usuarios.html', usuarios=lista_usuarios)


@app.route('/registrar-movimiento', methods=['GET', 'POST'])
@app.route('/registrar_movimiento', methods=['GET', 'POST'])
def registrar_movimiento():
    if 'usuario' not in session:
        return redirect(url_for('login'))
        
    if request.method == 'POST':
        id_insumo = request.form.get('id_insumo')
        tipo = request.form.get('tipo')
        cantidad = int(request.form.get('cantidad', 0))
        lote_str = request.form.get('lote')
        fecha_venc_str = request.form.get('fecha_vencimiento')
        observacion = request.form.get('observacion', '')
        
        user_obj = Usuario.query.filter_by(nombre_usuario=session['usuario']).first()
        id_usuario = user_obj.id_usuario if user_obj else 1
        insumo_obj = Insumo.query.get(id_insumo)

        if not insumo_obj:
            flash('Insumo no encontrado', 'danger')
            return redirect(url_for('registrar_movimiento'))

        id_lote_val = None

        if tipo in ['CONSUMO', 'SALIDA']:
            lotes_disponibles = Lote.query.filter(Lote.id_insumo == id_insumo, Lote.stock_lote > 0).order_by(Lote.fecha_vencimiento.asc()).all()
            
            lote_usado_str = "general"
            if lotes_disponibles:
                lote_sugerido = lotes_disponibles[0]
                id_lote_val = lote_sugerido.id_lote
                lote_usado_str = lote_sugerido.numero_lote
                
                if lote_sugerido.stock_lote >= cantidad:
                    lote_sugerido.stock_lote -= cantidad
                else:
                    lote_sugerido.stock_lote = 0
            
            insumo_obj.stock_actual -= cantidad
            flash(f'✅ Consumo registrado con éxito (Lote sugerido por FeFO: {lote_usado_str}). Stock actual de {insumo_obj.nombre}: {insumo_obj.stock_actual}', 'success')
        else:
            insumo_obj.stock_actual += cantidad
            if lote_str and lote_str.strip() != "":
                fecha_venc_val = None
                if fecha_venc_str and fecha_venc_str.strip() != "":
                    try:
                        fecha_venc_val = date.fromisoformat(fecha_venc_str)
                    except Exception:
                        pass
                
                lote_existente = Lote.query.filter_by(numero_lote=lote_str.strip(), id_insumo=id_insumo).first()
                if lote_existente:
                    lote_existente.stock_lote += cantidad
                    if fecha_venc_val:
                        lote_existente.fecha_vencimiento = fecha_venc_val
                    id_lote_val = lote_existente.id_lote
                else:
                    nuevo_lote = Lote(
                        numero_lote=lote_str.strip(),
                        fecha_vencimiento=fecha_venc_val,
                        id_insumo=id_insumo,
                        stock_lote=cantidad
                    )
                    db.session.add(nuevo_lote)
                    db.session.commit()
                    id_lote_val = nuevo_lote.id_lote

            flash(f'✅ ¡Ingreso registrado con éxito! El stock actualizado de {insumo_obj.nombre} es: {insumo_obj.stock_actual}', 'success')

        nuevo_mov = Movimiento(
            fecha=date.today(),
            tipo=tipo,
            cantidad=cantidad,
            observacion=observacion,
            id_insumo=int(id_insumo),
            id_usuario=id_usuario,
            id_lote=id_lote_val
        )
        db.session.add(nuevo_mov)
        db.session.commit()
        
        if session.get('rol') == 'ADMIN':
            return redirect(url_for('admin_panel'))
        return redirect(url_for('operador_panel'))

    lista_insumos = obtener_insumos_seccion()
    
    lotes_por_insumo = {}
    for i in lista_insumos:
        lotes_i = Lote.query.filter(Lote.id_insumo == i.id_insumo, Lote.stock_lote > 0).order_by(Lote.fecha_vencimiento.asc()).all()
        lotes_por_insumo[str(i.id_insumo)] = [
            {
                "numero": l.numero_lote,
                "vencimiento": l.fecha_vencimiento.strftime('%Y-%m-%d') if l.fecha_vencimiento else 'Sin vencimiento',
                "stock": l.stock_lote
            } for l in lotes_i
        ]

    return render_template('registrar_movimiento.html', insumos=lista_insumos, lotes_por_insumo=lotes_por_insumo)


@app.route('/ver-movimientos')
@app.route('/ver_movimientos')
@app.route('/movimientos')
@app.route('/mis-movimientos')
def ver_movimientos():
    if 'usuario' not in session:
        return redirect(url_for('login'))
        
    insumos_seccion = obtener_insumos_seccion()
    ids_insumos = [i.id_insumo for i in insumos_seccion]
    
    rol = session.get('rol')
    id_usuario_actual = Usuario.query.filter_by(nombre_usuario=session.get('usuario')).first()
    id_usr = id_usuario_actual.id_usuario if id_usuario_actual else None
    
    query = Movimiento.query.order_by(Movimiento.id_movimiento.desc()).filter(Movimiento.id_insumo.in_(ids_insumos)) if ids_insumos else Movimiento.query.order_by(Movimiento.id_movimiento.desc()).filter(False)
    
    if rol == 'ADMIN':
        lista_movimientos = query.all()
    else:
        lista_movimientos = query.filter(Movimiento.id_usuario == id_usr).all()

        
    return render_template('movimientos.html', movimientos=lista_movimientos)


@app.route('/proveedores', methods=['GET', 'POST'])
def proveedores():
    if 'usuario' not in session:
        return redirect(url_for('login'))
        
    if request.method == 'POST':
        nombre = request.form.get('nombre')
        nuevo = Proveedor(nombre=nombre)
        db.session.add(nuevo)
        db.session.commit()
        return redirect(url_for('proveedores'))

    lista_proveedores = Proveedor.query.all()
    return render_template('proveedores.html', proveedores=lista_proveedores)


@app.route('/categorias', methods=['GET', 'POST'])
def categorias():
    if 'usuario' not in session:
        return redirect(url_for('login'))
        
    if request.method == 'POST':
        nombre = request.form.get('nombre')
        descripcion = request.form.get('descripcion')
        
        nueva = Categoria(nombre=nombre, descripcion=descripcion)
        db.session.add(nueva)
        db.session.commit()
        return redirect(url_for('categorias'))

    lista_categorias = Categoria.query.all()
    return render_template('categorias.html', categorias=lista_categorias)


@app.route('/cambiar-rol-usuario/<int:id_usuario>', methods=['POST'])
def cambiar_rol_usuario(id_usuario):
    if session.get('rol') != 'ADMIN':
        flash('Acceso denegado.', 'danger')
        return redirect(url_for('admin_panel'))
    
    usuario = Usuario.query.get_or_404(id_usuario)
    nuevo_rol = request.form.get('rol')
    
    if nuevo_rol in ['ADMIN', 'OPERADOR']:
        usuario.rol = nuevo_rol
        db.session.commit()
        flash(f'Rol de {usuario.nombre_completo} actualizado a {nuevo_rol}.', 'success')
    
    return redirect(url_for('gestion_usuarios'))


@app.route('/toggle-estado-usuario/<int:id_usuario>', methods=['POST'])
def toggle_estado_usuario(id_usuario):
    if session.get('rol') != 'ADMIN':
        flash('Acceso denegado.', 'danger')
        return redirect(url_for('admin_panel'))
    
    usuario = Usuario.query.get_or_404(id_usuario)
    
    
    usuario_actual = Usuario.query.filter_by(nombre_usuario=session.get('usuario')).first()
    if usuario_actual and usuario.id_usuario == usuario_actual.id_usuario:
        flash('No podés desactivar tu propia cuenta de administrador.', 'warning')
        return redirect(url_for('gestion_usuarios'))

    usuario.activo = not (usuario.activo if usuario.activo is not None else True)
    db.session.commit()
    
    estado_str = "activado" if usuario.activo else "desactivado"
    flash(f'El usuario {usuario.nombre_completo} fue {estado_str} con éxito.', 'info')
    
    return redirect(url_for('gestion_usuarios'))

AWS_ACCESS_KEY_ID = 'tu-access-key'
AWS_SECRET_ACCESS_KEY = 'tu-secret-key'
AWS_BUCKET_NAME = 'sigi-imagenes'
AWS_REGION = 'us-east-1'


def subir_archivo_a_s3(file):
    if not file:
        return None
    s3 = boto3.client(
        's3',
        aws_access_key_id=AWS_ACCESS_KEY_ID,
        aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
        region_name=AWS_REGION,
    )
    filename = secure_filename(file.filename)
    s3.upload_fileobj(file, AWS_BUCKET_NAME, filename)
    return f'https://{AWS_BUCKET_NAME}.s3.{AWS_REGION}.amazonaws.com/{filename}'



@app.context_processor
def inject_s3_url():
    return {
        'S3_URL': 'https://sigi-imagenes.s3.amazonaws.com/'
    }

@app.route('/pedido-sugerido')
def pedido_sugerido():
    insumos = Insumo.query.all()
    
    lista_sugeridos = []
    for insumo in insumos:
        sugerencia = insumo.stock_deseado - insumo.stock_actual
        if sugerencia > 0:
            lista_sugeridos.append({
                'id': insumo.id_insumo,
                'nombre': insumo.nombre,
                'stock_actual': insumo.stock_actual,
                'sugerencia': sugerencia,
                'unidad_medida': insumo.unidad_medida
            })
            
    return render_template('pedido_sugerido.html', insumos=lista_sugeridos)

def obtener_proximos_feriados():
    try:
        anio = datetime.now().year
        
        url = f"https://date.nager.at/api/v3/PublicHolidays/{anio}/AR"
        response = requests.get(url, timeout=5)
        
        if response.status_code == 200:
            feriados = response.json()
            hoy = datetime.now().date()
            proximos = []
            for f in feriados:
                fecha_str = f.get('date')
                if fecha_str:
                    fecha_feriado = datetime.strptime(fecha_str, '%Y-%m-%d').date()
                    if fecha_feriado >= hoy:
                        proximos.append({
                            'fecha': fecha_str,
                            'titulo': f.get('localName'), 
                            'tipo': 'Nacional'
                        })
            if proximos:
                print(">>> ¡CONEXIÓN REAL EXITOSA con Nager.Date API!")
                return proximos[:2]
    except Exception as e:
        print(f">>> Error de conexión con la API: {e}")
    
    return []


@app.route('/login/google')
def login_google():
    redirect_uri = url_for('authorize_google', _external=True)
    return google.authorize_redirect(redirect_uri)


@app.route('/auth/google/callback')
def authorize_google():
    token = google.authorize_access_token()
    user_info = token.get('userinfo')
    
    if user_info:
        email = user_info['email']
        nombre = user_info['name']
        
        
        usuario_bd = Usuario.query.filter_by(nombre_usuario=email).first()
        
        if not usuario_bd:
            
            usuario_bd = Usuario(
                nombre_usuario=email,
                contrasenia='oauth_google_placeholder',  
                rol='OPERADOR',
                nombre_completo=nombre,
                activo=True
            )
            db.session.add(usuario_bd)
            db.session.commit()
        
        
        session['usuario'] = usuario_bd.nombre_usuario
        session['nombre_completo'] = usuario_bd.nombre_completo
        session['rol'] = usuario_bd.rol
        
    return redirect(url_for('seleccion_seccion'))

if __name__ == '__main__':
    app.run(debug=True)
