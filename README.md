# TodoList
Backend para la gestion de tareas 

## Antes de Iniciar

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


# To-Do List Application

Este proyecto es una API para gestionar tareas (To-Do List) creada con **Java**, **Spring Boot**, y **R2DBC** para soportar operaciones reactivas con PostgreSQL. La API permite crear, eliminar, actualizar, alternar el estado de completado y filtrar tareas según varios criterios.

## Características

- **CRUD** completo para gestionar tareas.
- **Filtro** de tareas por ID, descripción y estado (completado o no).
- **Documentación** con Swagger/OpenAPI.
- **R2DBC** para conexión reactiva con PostgreSQL.
- **Creación automática** de esquemas en PostgreSQL.

## Tabla de Contenidos

1. [Requisitos](#requisitos)
2. [Configuración del Proyecto](#configuración-del-proyecto)
3. [Ejecución del Proyecto](#ejecución-del-proyecto)
4. [Endpoints de la API](#endpoints-de-la-api)
5. [Configuración de Base de Datos](#configuración-de-base-de-datos)
6. [Documentación API](#documentación-api)
7. [Tecnologías Utilizadas](#tecnologías-utilizadas)

## Requisitos

- **Java 17** o superior
- **PostgreSQL** para la base de datos
- **Docker** (opcional, para ejecutar PostgreSQL en un contenedor)

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
       url: r2dbc:postgresql://localhost:5432/todolist
       username: tu_usuario
       password: tu_contraseña
   ```

2. **Opción 2: Docker**
   - Si prefieres usar Docker, ejecuta el siguiente comando para iniciar un contenedor con PostgreSQL:

     ```bash
     docker run --name postgres-todolist -e POSTGRES_DB=todolist -e POSTGRES_USER=tu_usuario -e POSTGRES_PASSWORD=tu_contraseña -p 5432:5432 -d postgres
     ```

### Paso 3: Crear la Tabla Automáticamente

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
    descripcion VARCHAR(255) NOT NULL,
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

---

Este README debería ayudarte a configurar y entender el proyecto. ¡Disfruta programando!