# CiviFix — Java, XML y Compose

Aplicación Android para reportar incidencias de Cajamarca. Esta versión amplía el proyecto original y mantiene su paquete `com.example.project_caxfix` y su base de datos `civifix.db`.

## Tecnologías

- Java: actividades, validación, sesión, base de datos, reportes, imágenes y comentarios.
- XML y ViewBinding: pantallas de login, registro, reportes, detalle, perfil y formulario.
- Kotlin y Jetpack Compose: únicamente el resumen de reportes del perfil (`compose/ProfileSummary.kt`).
- SQLite: cuentas y datos locales. Los usuarios creados en un dispositivo no se sincronizan con otro.
- Android 8.0/API 26 o superior; compilación con SDK 35, AGP 8.9.2, Gradle 8.11.1 y Kotlin 2.1.20. Usa JDK 17 para Gradle.

## Funciones implementadas

1. Registro con nombre, correo, DNI, barrio y contraseña. El DNI es un identificador local y no se verifica contra RENIEC.
2. Inicio de sesión por correo o DNI; contraseñas con PBKDF2 y sal aleatoria; sesión persistente y cierre de sesión.
3. Listado de reportes y filtros por categoría.
4. Creación de reportes con foto opcional, título, categoría, descripción y dirección. Permite ocultar el nombre en el listado público.
5. Fotografías copiadas al almacenamiento privado de la aplicación, con un límite de 16 MB.
6. Detalle del reporte, apoyo reversible y comentarios guardados.
7. Mi actividad: reportes propios. Perfil: editar nombre/barrio, teléfono local, foto y resumen en Compose.
8. Eliminación de reportes propios, con confirmación; sus comentarios y apoyos se eliminan también.
9. Migración de la base de datos anterior sin borrar sus reportes.
10. Vista previa de fotos, borrador por cuenta, descarte y apertura de una aplicación de mapas con la dirección escrita.
11. Acciones de apoyar, comentar y compartir en las tarjetas; barra de navegación con acceso al formulario.
12. Preferencias de alertas guardadas localmente, sin servicio de envío conectado.

Los tres reportes que aparecen en una instalación nueva son ejemplos. Los reportes anteriores sin una cuenta asociada se conservan en el listado general; no se atribuyen automáticamente a la primera cuenta registrada.

## Abrir en Android Studio

Para abrir una copia independiente, selecciona la carpeta `projectcaxfix` desde **File → Open**. Deja terminar la sincronización de Gradle. Si se solicita SDK 35, instálalo desde SDK Manager. Selecciona un emulador o conecta tu teléfono y pulsa Run.

Para continuar en el mismo proyecto y conservar Git, sigue `LEEME-Android-Studio-y-Git.md` en la raíz del paquete. No necesitas crear otra Empty Views Activity ni ejecutar `git init`.

## Probar la app

Crea tu cuenta desde el login. Publica un reporte con el botón +. Prueba los filtros, abre el detalle, agrega y retira un apoyo y escribe un comentario. Revisa Mi actividad y Perfil. Cierra y abre la app para comprobar la sesión. Cierra sesión e ingresa con el DNI. Crea una segunda cuenta para comprobar que solo el autor puede eliminar un reporte.

## Código principal

| Archivo | Responsabilidad |
|---|---|
| `MainActivity.java` / `RegisterActivity.java` | Login y registro |
| `HomeActivity.java` / `ActivityActivity.java` | Reportes generales y propios |
| `NewReportBottomSheet.java` / `NewReportViewModel.java` | Formulario y publicación que continúa al girar la pantalla |
| `DetailActivity.java` | Detalle, apoyos, comentarios y eliminación |
| `ProfileActivity.java` | Cuenta y edición de perfil |
| `data/CiviFixDatabaseHelper.java` | Tablas SQLite y migración |
| `data/UserDao.java` / `data/ReportDao.java` | Consultas y operaciones |
| `security/PasswordHasher.java` | Protección y verificación de contraseñas |
| `compose/ProfileSummary.kt` | Resumen pequeño en Compose invocado desde Java |

## Pruebas

En la terminal de Android Studio, en Windows:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Para ejecutar el flujo de integración de SQLite y cuentas, conecta un emulador o teléfono y ejecuta:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Alcance pendiente

La app es un prototipo local. No incluye un servidor municipal, sincronización entre celulares, captura GPS, notificaciones remotas, acceso con Google, DNI electrónico ni recuperación de contraseña por correo. El mapa incluido es una imagen de referencia, y el botón abre una aplicación de mapas instalada. Los estados municipales del ejemplo se muestran, pero no hay un panel administrativo que los cambie. Google, DNIe y recuperación se muestran como en el diseño y explican que su integración está pendiente al pulsarlos.

## Diseño y recursos

La versión 3 adapta las cuatro pantallas del [Figma del proyecto](https://www.figma.com/design/1NbgFTkymvqYjiq3n48WPe/) a Java/XML y conserva el resumen Compose. Usa verde `#004635`, fondo `#F9F9FF`, superficies `#F0F3FF`, tarjetas blancas de 12 dp y Plus Jakarta Sans. Los contadores y la identidad se calculan con los datos del dispositivo; no se copian las cifras ficticias del diseño.

Las fotos alternativas están en `app/src/main/res/drawable-nodpi`. Los 32 iconos están en `res/drawable-xxhdpi`, exportados a escala 3 para conservar su tamaño lógico en dp. Las vistas controlan su tamaño. Los SVG originales están en `res/raw`. No necesitas descargar nada cuando ejecutas la app. Las fotos aportadas por el usuario se guardan por separado en el almacenamiento privado.

Las fotos originales del Figma no se pudieron descargar completas antes de alcanzar el límite de consultas. Se usaron fotos de Wikimedia Commons autorizadas como alternativa por el alumno. Sus autores, fuentes, licencias y cambios están en `res/raw/creditos_imagenes.txt`; el inicio de sesión permite consultar esos créditos. El mapa de referencia incluye atribución a OpenStreetMap. El recorte de las fotos y los datos de usuario pueden diferir del Figma.

Las 22 clases Java existentes se conservan: pantallas, modelos de datos, acceso SQLite y utilidades. Una clase separada permite localizar y explicar cada responsabilidad. Esta actualización no añade más clases Java.

## Documentación oficial

- [Compose en pantallas con Views/XML](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views)
- [Configuración del compilador Compose](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler)
- [Compatibilidad de Android Gradle Plugin 8.9](https://developer.android.com/build/releases/agp-8-9-0-release-notes)
