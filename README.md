# 🛒 Gestión de Inventario - Sistema de Punto de Venta (POS)

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-00758f.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Sistema profesional de gestión de inventario con interfaz gráfica desarrollado en Java.**

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Logging](#logging)
- [Base de Datos](#base-de-datos)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

---

## 📝 Descripción

**Gestión de Inventario** es una aplicación Java completa para administrar productos, inventario y ventas. Ofrece una
interfaz gráfica intuitiva, gestión robusta de datos con transacciones ACID, y logging profesional para auditoría.

### Casos de Uso Principales

- ✅ **Gestión de Productos**: Crear, leer, actualizar y eliminar productos
- ✅ **Control de Inventario**: Monitorear stock en tiempo real
- ✅ **Punto de Venta**: Procesar ventas con carrito de compras
- ✅ **Búsqueda Avanzada**: Filtrar productos por nombre o criterios
- ✅ **Auditoría**: Logging completo de todas las operaciones
- ✅ **Pool de Conexiones**: Rendimiento optimizado con HikariCP

---

## ✨ Características

### Core Features

| Característica             | Descripción                           |
|----------------------------|---------------------------------------|
| 🗄️ **CRUD Completo**      | Operaciones completas sobre productos |
| 🔍 **Búsqueda Avanzada**   | Filtrado por nombre y características |
| 💰 **Punto de Venta**      | Sistema POS integrado con carrito     |
| 📊 **Gestión de Stock**    | Control de inventario en tiempo real  |
| 🔐 **Validación Robusta**  | Validaciones con mensajes claros      |
| 📝 **Logging Profesional** | SLF4J + Logback para auditoría        |
| 🔄 **Transacciones ACID**  | Garantía de consistencia de datos     |
| 🏊 **Connection Pool**     | HikariCP para alto rendimiento        |

### Seguridad y Calidad

- ✅ Credenciales externalizadas (no hardcodeadas)
- ✅ Validación de entrada en todos los campos
- ✅ Excepciones personalizadas (`ValidationException`)
- ✅ Manejo robusto de errores
- ✅ Logging estructurado con niveles (DEBUG, INFO, WARN, ERROR)
- ✅ Constraints SQL (CHECK) para integridad de datos
- ✅ Índices de base de datos para optimización

---

## 🛠️ Requisitos

### Antes de instalar

- **Java Development Kit (JDK)**: Versión 17 o superior
    - Descargar: https://www.oracle.com/java/technologies/downloads/
    - Verificar: `java -version`

- **Apache Maven**: Versión 3.8 o superior
    - Descargar: https://maven.apache.org/download.cgi
    - Verificar: `mvn -version`

- **MySQL Server**: Versión 8.0 o superior
    - Descargar: https://www.mysql.com/downloads/
    - Verificar: `mysql --version`

- **Git**: Para clonar el repositorio
    - Descargar: https://git-scm.com/

---

## 📦 Instalación

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/Matute0512/gestion-inventario-java.git
cd gestion-inventario-java
```

### Paso 2: Configurar la Base de Datos

#### 2.1 Crear la base de datos

```bash
mysql -u root -p
```

Luego en MySQL:

```sql
-- Crear base de datos
CREATE
DATABASE gestion_inventario;
USE
gestion_inventario;

-- Ejecutar script de schema
SOURCE
schema.sql;
```

O desde línea de comandos:

```bash
mysql -u root -p < schema.sql
```

#### 2.2 Verificar las tablas

```sql
SHOW
TABLES;
DESCRIBE products;
```

Debería mostrar:

- Tabla `products` con columnas: `id`, `name`, `description`, `price`, `stock`
- Constraints: `price > 0`, `stock >= 0`
- Índices: `idx_name`, `idx_stock`

---

## ⚙️ Configuración

### Configurar credenciales de BD

#### Opción A: Variables de Entorno (Recomendado)

```bash
# En Windows (PowerShell)
$env:DB_USER = "root"
$env:DB_PASSWORD = "tu_contraseña"

# En Windows (CMD)
set DB_USER=root
set DB_PASSWORD=tu_contraseña

# En Linux/Mac
export DB_USER=root
export DB_PASSWORD=tu_contraseña
```

#### Opción B: Archivo de propiedades

Edita `src/main/resources/application.properties`:

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_inventario
spring.datasource.username=root
spring.datasource.password=tu_contraseña
# HikariCP
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=5000
```

### Configurar Logging

El archivo `src/main/resources/logback.xml` ya está configurado para:

- **Consola**: Logs con colores y formato legible
- **Archivo**: `logs/gestion-inventario.log` con rotación automática
- **Niveles**: DEBUG para desarrollo, INFO para producción

No requiere cambios adicionales.

---

## 🚀 Uso

### Ejecutar la aplicación

#### Opción A: Desde IntelliJ IDEA

1. Abre el proyecto en IntelliJ
2. Click derecho en `App.java`
3. Selecciona `Run 'App.main()'`
4. La aplicación se abrirá

#### Opción B: Desde Maven

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn javafx:run
```

#### Opción C: Ejecutable JAR

```bash
# Crear JAR
mvn clean package

# Ejecutar
java -jar target/gestion-inventario-1.0-SNAPSHOT.jar
```

---

### Funcionalidades Principales

#### 1. **Gestión de Productos**

**Agregar Producto:**

1. En la pestaña "Gestión Inventario"
2. Rellena: Nombre, Precio, Stock, Descripción (opcional)
3. Haz clic en "Agregar Producto"
4. Verás confirmación en mensaje emergente

**Buscar Producto:**

1. Usa el campo "Buscar" en la tabla
2. Escribe parte del nombre
3. Los resultados se filtran automáticamente

**Actualizar Producto:**

1. Haz clic en un producto en la tabla
2. Modifica los campos
3. Haz clic en "Guardar cambios"

**Eliminar Producto:**

1. Selecciona un producto
2. Haz clic en "Eliminar Producto"
3. Confirma la eliminación

#### 2. **Punto de Venta**

1. Abre la pestaña "Punto de Venta"
2. Selecciona productos del inventario
3. Especifica cantidad
4. El carrito se actualiza automáticamente
5. Haz clic en "Registrar Venta"
6. Se descuenta del inventario automáticamente

#### 3. **Monitoreo del Pool de Conexiones**

1. En la interfaz, busca "Estado del Pool"
2. Muestra: conexiones activas, en espera, disponibles
3. Indica rendimiento de la BD

---

## 🏗️ Arquitectura

### Estructura del Proyecto

```
gestion-inventario/
├── src/main/java/io/github/torres/
│   ├── config/
│   │   └── DatabaseConnection.java       # Configuración de HikariCP
│   ├── controller/
│   │   └── ProductController.java        # Lógica de negocio
│   ├── dao/
│   │   └── ProductDAO.java               # Acceso a datos
│   ├── exception/
│   │   └── ValidationException.java      # Excepciones personalizadas
│   ├── model/
│   │   ├── CartItem.java                 # Modelo de carrito
│   │   └── Product.java                  # Modelo de producto
│   ├── util/
│   │   └── ValidationUtil.java           # Utilidades de validación
│   ├── view/
│   │   ├── MainView.java                 # Ventana principal
│   │   ├── panels/
│   │   │   ├── InventoryPanel.java       # Panel de inventario
│   │   │   └── SalesPanel.java           # Panel de ventas
│   │   └── styles/
│   │       ├── Theme.java                # Constantes de UI
│   │       └── UIStyles.java             # Estilos de componentes
│   └── App.java                          # Punto de entrada
├── src/main/resources/
│   ├── application.properties             # Config por defecto
│   ├── application-prod.properties        # Config producción
│   └── logback.xml                        # Configuración de logging
├── pom.xml                               # Dependencias Maven
├── schema.sql                            # Schema de BD
└── README.md                             # Este archivo
```

### Flujo de Datos

```
┌─────────────────────┐
│   Interfaz Gráfica  │
│   (MainView)        │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│  ProductController  │  ← Lógica de negocio
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│   ProductDAO        │  ← Acceso a datos
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│  DatabaseConnection │  ← Pool HikariCP
│  (MySQL)            │
└─────────────────────┘
```

### Patrones Utilizados

- **MVC**: Model-View-Controller para separación de responsabilidades
- **DAO**: Data Access Object para acceso a la BD
- **Singleton**: DatabaseConnection con pool de conexiones único
- **Exception Handling**: Excepciones personalizadas y try-catch
- **Logging**: SLF4J + Logback para auditoría

---

## 📚 Tecnologías

### Backend

| Tecnología | Versión | Uso                       |
|------------|---------|---------------------------|
| Java       | 17+     | Lenguaje principal        |
| Maven      | 3.8+    | Gestor de dependencias    |
| SLF4J      | 2.0.9   | API de logging            |
| Logback    | 1.4.11  | Implementación de logging |
| HikariCP   | 5.1.0   | Pool de conexiones        |
| MySQL      | 8.0+    | Base de datos             |
| JDBC       | 8.0.33  | Driver MySQL              |

### Frontend

| Tecnología | Versión  | Uso                     |
|------------|----------|-------------------------|
| JavaFX     | 20.0.1   | Interfaz gráfica        |
| Swing      | Incluido | Componentes adicionales |

### Herramientas

- **Git**: Control de versiones
- **IntelliJ IDEA**: IDE de desarrollo
- **MySQL Workbench**: Administración de BD

---

## 📝 Logging

### Niveles de Log

```
DEBUG  → Información detallada para debugging
INFO   → Eventos importantes
WARN   → Situaciones anormales
ERROR  → Errores que no detienen la app
FATAL  → Errores críticos
```

### Ejemplos de Logs

```log
17:45:23.123 [main] INFO  DatabaseConnection - ✅ Pool de conexiones HikariCP inicializado
17:45:24.456 [AWT-EventQueue-0] DEBUG ProductController - Intentando agregar nuevo producto...
17:45:24.789 [AWT-EventQueue-0] INFO  ProductDAO - Producto guardado correctamente: Monitor
17:45:25.012 [AWT-EventQueue-0] ERROR ProductDAO - ❌ Error al conectar con BD: Connection refused
```

### Ubicaciones

- **Consola**: Logs en tiempo real durante ejecución
- **Archivo**: `logs/gestion-inventario.log` (automáticamente creado)
- **Rotación**: Máximo 10MB por archivo, retención de 30 días

---

## 🗄️ Base de Datos

### Schema SQL

```sql
CREATE TABLE products
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL UNIQUE,
    description VARCHAR(1000),
    price       DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stock       INT            NOT NULL CHECK (stock >= 0),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_name ON products (name);
CREATE INDEX idx_stock ON products (stock);
```

### Constraints

| Constraint           | Descripción                 |
|----------------------|-----------------------------|
| `PRIMARY KEY (id)`   | Identificador único         |
| `UNIQUE (name)`      | No duplicar nombres         |
| `CHECK (price > 0)`  | Precio debe ser positivo    |
| `CHECK (stock >= 0)` | Stock no puede ser negativo |
| `TIMESTAMP`          | Auditoría de cambios        |

### Índices

| Índice      | Propósito                    |
|-------------|------------------------------|
| `idx_name`  | Acelera búsquedas por nombre |
| `idx_stock` | Acelera filtros por stock    |

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para contribuir:

1. **Fork** el repositorio
2. **Crea una rama** con tu feature: `git checkout -b feature/AmazingFeature`
3. **Commit** tus cambios: `git commit -m 'feat: Add amazing feature'`
4. **Push** a la rama: `git push origin feature/AmazingFeature`
5. **Abre un Pull Request**

### Convenciones de Commits

```
feat:     Nueva funcionalidad
fix:      Corrección de bug
refactor: Refactorización de código
docs:     Cambios de documentación
style:    Cambios de formato
test:     Agregar/actualizar tests
chore:    Cambios en dependencias
```

---

## 📋 Pendientes (Roadmap)

- [ ] Agregar tests unitarios con JUnit 5
- [ ] Implementar Service Layer
- [ ] Crear DTO para transferencia de datos
- [ ] Agregar validaciones más complejas
- [ ] Implementar búsqueda avanzada con SQL dinámico
- [ ] Crear reportes en PDF
- [ ] Agregar autenticación de usuarios
- [ ] Implementar API REST
- [ ] Dockerizar la aplicación
- [ ] Agregar base de datos en la nube

---

## 🐛 Problemas Conocidos

### Error de SLF4J

**Síntoma**: Mensaje de advertencia sobre `StaticLoggerBinder`

**Solución**: Ya incluido en `pom.xml` (logback-classic v1.4.11)

### Conexión a MySQL rechazada

**Síntoma**: `SQLException: Connection refused`

**Solución**:

1. Verifica que MySQL está corriendo: `mysql --version`
2. Verifica las credenciales en `application.properties`
3. Verifica que la BD `gestion_inventario` existe

### Tabla no existe

**Síntoma**: `SQLException: Table 'gestion_inventario.products' doesn't exist`

**Solución**:

```bash
mysql -u root -p < schema.sql
```

---

## 📄 Licencia

Este proyecto está bajo la licencia **MIT**. Ver archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2026 Matias

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👤 Autor

**Matias** - [@Matute0512](https://github.com/Matute0512)

- 🇦🇷 Desarrollador desde Argentina
- 💻 Especializado en Java y desarrollo de software
- 🚀 Apasionado por código limpio y buenas prácticas

---

## 📧 Contacto y Soporte

- **GitHub Issues**: Reporta bugs en [GitHub Issues](https://github.com/Matute0512/gestion-inventario-java/issues)
- **Email**: matias@example.com
- **LinkedIn**: [Matias](https://linkedin.com/in/matias)

---

## 🙏 Agradecimientos

- JavaFX por la excelente librería de UI
- HikariCP por el pool de conexiones
- SLF4J + Logback por el logging profesional
- La comunidad de Java por el soporte continuo

---

## 📊 Estadísticas del Proyecto

- **Líneas de Código**: ~2,500
- **Archivos Java**: 13
- **Commits**: 8+
- **Versión**: 1.0.0
- **Estado**: ✅ Funcional y listo para producción

---

**Última actualización**: 2026-05-26  
**Versión**: 1.0.0  
**Estado**: ✅ Producción

