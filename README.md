# Gestor Personal de Tareas

Descripción: Aplicación Android diseñada para administrar tareas personales mediante un esquema de seguridad estricto donde cada usuario accede exclusivamente a su información. Permite ejecutar operaciones CRUD remotas en la nube y guardar borradores locales en el dispositivo cuando no hay conexión.

Integrantes: Kevin Alexis Mesa Delgado
Ficha:3223874

---

## Tecnologías y Arquitectura
El proyecto fue construido utilizando Clean Architecture (Arquitectura Limpia) para separar responsabilidades, en conjunto con el patrón MVVM (Model-View-ViewModel).

- Lenguaje y UI: Kotlin y Jetpack Compose.
- Base de Datos y Nube: Firebase (Authentication y Cloud Firestore) para la gestión remota, y Room para la persistencia local de borradores.
- Inyección de Dependencias: Dagger Hilt procesado mediante KSP (Kotlin Symbol Processing).
- Asincronía y Estados: Corrutinas de Kotlin (`viewModelScope.launch`) y `StateFlow` (`UiState`) para evitar bloqueos en el hilo principal.
- Navegación: Jetpack Navigation Compose para el enrutamiento mediante estados.

---

## Explicación de la Estructura de Paquetes
La organización del código refleja la separación estricta de responsabilidades de la Arquitectura Limpia:

- `domain/` (Capa de Reglas de Negocio): Es la capa más pura y no depende de ninguna librería de Android. Contiene los modelos de datos base, las interfaces de los repositorios y los Casos de Uso individuales (ej. `CreateTaskUseCase`, `GetTasksUseCase`).
- `data/` (Capa de Datos): Conecta la lógica limpia de la aplicación con el mundo exterior. Contiene las entidades locales de Room, los DTOs, los mapeadores y las implementaciones reales de los repositorios que se comunican con Firebase Auth y Cloud Firestore.
-  `ui/` (Capa de Presentación): Agrupa todo lo que el usuario ve y con lo que interactúa. Contiene los componentes de Jetpack Compose (`screen/`), el estado de la interfaz (`state/`) y los controladores `ViewModels` que envían las órdenes hacia los Casos de Uso.
-  `di/` (Inyección de Dependencias): Alberga los módulos (`DatabaseModule`, `FirebaseModule`, `RepositoryModule`) que le enseñan a Hilt cómo construir objetos complejos (como las bases de datos) y entregárselos automáticamente a los ViewModels.

---

## Instrucciones para configurar y ejecutar el proyecto
1. Clonar el repositorio:
   Abre la terminal o la opción de control de versiones en Android Studio y ejecuta el comando de clonación utilizando la URL del proyecto:
   `git clone https://github.com/kevinmesadelgado-maker/Geestor_tareas-android.git`
2. Configurar credenciales de Firebase:
   Asegúrate de agregar el archivo `google-services.json` (descargado desde la consola de tu proyecto de Firebase) dentro del directorio `app/` para que la autenticación y Cloud Firestore funcionen correctamente.
3. Sincronizar Gradle:
   Haz clic en Sync Project with Gradle Files para descargar las dependencias, incluyendo las herramientas de Hilt y Compose.
4. Ejecución:
   Selecciona un emulador o conecta un dispositivo físico y presiona el botón verde de Run (`Shift + F10`).

---

## Funcionalidades Terminadas y Errores Conocidos

Funcionalidades terminadas:
- Registro, inicio de sesión y cierre de sesión de usuarios con Firebase Auth.
- Protección de rutas (no se puede volver atrás a pantallas protegidas tras cerrar sesión).
- Operaciones CRUD completas (Crear, Leer, Actualizar, Eliminar) en Cloud Firestore, filtradas estrictamente por el `ownerId` del usuario autenticado.
- Uso de reglas de seguridad directamente en Firestore para validar la propiedad de los documentos.
- Sistema funcional de guardado de borradores locales usando Room Database.
- Publicación exitosa de borradores locales hacia la nube de Firestore.

Errores conocidos (Known Bugs) y Resoluciones:
- Conflictos de Dependencias (Gradle): Durante el desarrollo se presentaron errores de compilación al intentar alinear las librerías en el archivo `build.gradle.kts (Module :app)`. Fue necesario ajustar cuidadosamente las versiones de dependencias modernas (como Jetpack Compose, Navigation y Retrofit) junto con sus respectivos procesadores KSP (`ksp(libs.hilt.compiler)` y `ksp(libs.moshi.kotlin.codegen)`) para lograr un build exitoso.
- Fallas de Inyección Inicial: Inicialmente la aplicación sufría cierres inesperados porque el sistema Hilt no lograba inyectar los ViewModels. Esto se solucionó recordando el paso obligatorio de registrar la clase base (que contiene `@HiltAndroidApp`) dentro de la etiqueta `<application>` en el archivo `AndroidManifest.xml`

---

## Capturas de las Pantallas Principales

- Pantalla de Login y Registro:
  ![LoginScreen](img.png)

- Listado de Tareas (Filtrado por usuario real):
  ![TaskScreen](img_1.png)

- Formulario de Tareas y Borradores (Room):
  ![TaskFormScreen](img_3.png))