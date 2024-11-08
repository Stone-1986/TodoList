# TodoList

Backend para la gestión de tareas utilizando una arquitectura hexagonal y programación reactiva.

## Características

- **CRUD** completo para tareas.
- **Filtro** de tareas por ID, título, descripción y estado.
- **Documentación** con Swagger/OpenAPI.
- **Conexión Reactiva** con PostgreSQL utilizando R2DBC.
- **Creación Automática** de esquemas en PostgreSQL.

## Tabla de Contenidos

1. [Requisitos](#requisitos)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Configuración del Proyecto](#configuración-del-proyecto)
4. [Ejecución del Proyecto](#ejecución-del-proyecto)
5. [Endpoints de la API](#endpoints-de-la-api)
6. [Configuración de Base de Datos](#configuración-de-base-de-datos)
7. [Documentación API](#documentación-api)
8. [Tecnologías Utilizadas](#tecnologías-utilizadas)
9. [Clean Architecture](#Clean Architecture)

## Requisitos

- **Java 17** o superior
- **PostgreSQL**
- **Docker** (opcional, para ejecutar PostgreSQL en un contenedor)
- 
## Estructura del Proyecto

El proyecto sigue una arquitectura hexagonal y está organizado en los siguientes módulos:

- **applications/app-service**: Contiene la configuración de la aplicación y la clase principal `MainApplication`.
- **domain**: Define la lógica de negocio.
    - **model**: Entidades de dominio.
    - **usecase**: Casos de uso del sistema.
- **infrastructure**: Adaptadores de infraestructura y puntos de entrada.
    - **driven-adapters/r2dbc-postgresql**: Persistencia con R2DBC para PostgreSQL.
    - **entry-points/reactive-web**: API HTTP con WebFlux.

## Configuración del Proyecto

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/usuario/todolist-project.git
cd todolist-project
```

### Paso 2: Configurar Base de Datos

1. **Opción 1: Base de Datos Local**
   - Crear una base de datos en PostgreSQL llamada `todolist`.
   - Configura las credenciales en el archivo `application.yml`.

   ```yaml
   spring:
     r2dbc:
      database: "todolist"
      schema: "public"
      username: "nombre de usuario"
      password: "password"
      host: "localhost"
      port: "5432"
   ```

2. **Opción 2: Docker**
   - Si prefieres usar Docker, realiza las siguientes acciones
    
   - **Paso 1: Compilar el proyecto en la raíz:**
   - ```bash
       ./gradlew clean build -x test 
     ```
   - **PPaso 2: Construir y ejecutar Docker Compose desde deployment \todo_list\deployment.**

   - ```bash
       docker-compose up --build -d
     ```
   - **Paso 3: Verificar los logs de java_app para confirmar el inicio correcto**

   - ```bash
       docker-compose logs java_app
     ```
   - **Paso 4: Si todo parece estar bien en los logs, intenta acceder a tu aplicación en http://localhost:8080**


### Paso 3: Creación Automática de Tablas

Este proyecto usa un archivo `schema.sql` para crear automáticamente la tabla `task` en PostgreSQL al iniciar la aplicación. Asegúrate de tener esta configuración en `application.yml`:

```yaml
spring:
  r2dbc:
    schema: classpath:schema.sql
```

## Ejecución del Proyecto

Puedes ejecutar la aplicación usando Gradle o directamente desde un IDE:

```bash
./gradlew bootRun
```

La API estará disponible en `http://localhost:8080/api/tasks`.

## Endpoints de la API

La API permite realizar operaciones CRUD sobre las tareas. A continuación, se describen algunos de los endpoints:

| Método | Endpoint               | Descripción                                          |
|--------|-------------------------|------------------------------------------------------|
| POST   | `/api/tasks`            | Crea una nueva tarea                                 |
| GET    | `/api/tasks`            | Lista tareas con filtros opcionales en el cuerpo     |
| PUT    | `/api/tasks/{id}`       | Actualiza los datos de una tarea                     |
| PUT    | `/api/tasks/{id}/toggle`| Alterna el estado de completado de una tarea         |
| DELETE | `/api/tasks/{id}`       | Elimina una tarea por su ID                          |

### Ejemplo de Cuerpo de Solicitud para Filtrar Tareas

Para usar el filtro de tareas con `POST /api/tasks/filter`, envía un cuerpo como este:

```json
{
  "id": "12345",
  "description": "Compra de comestibles",
  "completed": false
}
```

## Configuración de Base de Datos

El proyecto está configurado para crear automáticamente el esquema en PostgreSQL. Puedes encontrar el script de creación de tablas en `src/main/resources/schema.sql`:

```sql
CREATE TABLE IF NOT EXISTS public.task (
    id VARCHAR(36) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL
);
```
## Documentación API

La documentación de la API se genera automáticamente con **Swagger** y está disponible en `http://localhost:8080/swagger-ui.html` una vez que la aplicación esté en ejecución.

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring WebFlux** (para programación reactiva)
- **R2DBC** (conector de base de datos reactivo)
- **PostgreSQL**
- **Swagger/OpenAPI** (documentación de API)

## Clean Architecture

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando
con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el
artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio
mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de
aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de
entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de
diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las
dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias
concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde
encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

---

Este README debería ayudarte a configurar y entender el proyecto.