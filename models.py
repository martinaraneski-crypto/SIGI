from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

class Usuario(db.Model):
    __tablename__ = 'usuario'
    id_usuario = db.Column(db.Integer, primary_key=True)
    nombre_usuario = db.Column(db.String(50), unique=True, nullable=False)
    contrasenia = db.Column(db.String(255), nullable=False)
    rol = db.Column(db.String(20), nullable=False)
    nombre_completo = db.Column(db.String(100), nullable=False)
    activo = db.Column(db.Boolean, nullable=True)
    movimientos = db.relationship('Movimiento', backref='usuario', lazy=True)


class Proveedor(db.Model):
    __tablename__ = 'proveedor'
    id_proveedor = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(100), nullable=False)


class Categoria(db.Model):
    __tablename__ = 'categoria'
    id_categoria = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(50), nullable=False)
    descrpcion = db.Column(db.String(200), nullable=True)  
    id_categoria_padre = db.Column(db.Integer, nullable=True)
    insumos = db.relationship('Insumo', backref='categoria', lazy=True)


insumo_proveedor = db.Table('insumo_proveedor',
    db.Column('id_insumo', db.Integer, db.ForeignKey('insumo.id_insumo'), primary_key=True),
    db.Column('id_proveedor', db.Integer, db.ForeignKey('proveedor.id_proveedor'), primary_key=True)
)


class Insumo(db.Model):
    __tablename__ = 'insumo'
    id_insumo = db.Column(db.Integer, primary_key=True)
    codigo_propio = db.Column(db.String(50), nullable=True)
    nombre = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(200), nullable=True)
    stock_actual = db.Column(db.Integer, nullable=False, default=0)
    stock_minimo = db.Column(db.Integer, nullable=False, default=0)
    stock_deseado = db.Column(db.Integer, nullable=False, default=0)
    unidad_medida = db.Column(db.String(20), nullable=False)
    id_categoria = db.Column(db.Integer, db.ForeignKey('categoria.id_categoria'), nullable=True)
    
    
    proveedores = db.relationship('Proveedor', secondary=insumo_proveedor, backref=db.backref('insumos', lazy=True))
    
    lotes = db.relationship('Lote', backref='insumo', lazy=True)
    movimientos = db.relationship('Movimiento', backref='insumo', lazy=True)



class Lote(db.Model):
    __tablename__ = 'lote'
    id_lote = db.Column(db.Integer, primary_key=True)
    numero_lote = db.Column(db.String(50), nullable=False)
    fecha_vencimiento = db.Column(db.Date, nullable=True)
    id_insumo = db.Column(db.Integer, db.ForeignKey('insumo.id_insumo'), nullable=False)
    stock_lote = db.Column(db.Integer, nullable=False, default=0)
    movimientos = db.relationship('Movimiento', backref='lote', lazy=True)


class Movimiento(db.Model):
    __tablename__ = 'movimiento'
    id_movimiento = db.Column(db.Integer, primary_key=True)
    fecha = db.Column(db.Date, nullable=False)
    tipo = db.Column(db.String(20), nullable=False)  
    cantidad = db.Column(db.Integer, nullable=False)
    observacion = db.Column(db.String(200), nullable=True)
    id_insumo = db.Column(db.Integer, db.ForeignKey('insumo.id_insumo'), nullable=False)
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuario.id_usuario'), nullable=False)
    id_lote = db.Column(db.Integer, db.ForeignKey('lote.id_lote'), nullable=True)