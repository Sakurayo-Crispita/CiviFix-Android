# Recursos de la interfaz

Los drawables ya forman parte del proyecto; no hay que descargarlos desde la app.

| Recurso en `app/src/main/res/drawable-nodpi` | Uso |
|---|---|
| `img_cajamarca_plaza.webp` | Tarjeta de bienvenida del inicio de sesión |
| `demo_pothole.webp` | Foto ilustrativa del reporte de baches |
| `demo_lighting.webp` | Foto ilustrativa del reporte de alumbrado |
| `demo_resolved.webp` | Foto ilustrativa del reporte solucionado |
| `img_mapa_cajamarca.png` | Mapa de referencia en el formulario |


`drawable-nodpi` es un directorio válido de recursos drawable. En Android Studio aparece agrupado dentro de `res → drawable`. Se referencia igual que `drawable`:

```xml
android:src="@drawable/img_cajamarca_plaza"
```

Las medidas, el fondo y el recorte se definen en los XML. No muevas estos recursos a `mipmap`, que contiene los iconos de lanzamiento.

Los 32 iconos `ic_figma_*.png` están en `res/drawable-xxhdpi`, a escala 3. El sufijo xxhdpi permite que Android interprete el tamaño original del icono en lugar de triplicarlo.

Los SVG fuente están en `res/raw/figma_*.svg`. Android usa las copias PNG de esos mismos iconos para no añadir una biblioteca de SVG ni nuevas clases Java. Plus Jakarta Sans está en `res/font`, con su licencia en `res/raw/jakarta_ofl.txt`.

Las fotos alternativas proceden de Wikimedia Commons, con licencia CC BY-SA 4.0. Sus fuentes y autores aparecen en `res/raw/creditos_imagenes.txt` y en el enlace de créditos del login. Las fotos de ejemplo ilustran el prototipo; no prueban incidencias reales. El mapa incluye crédito a OpenStreetMap. Las fotografías elegidas por cada usuario no son drawables: se copian al almacenamiento privado del dispositivo.
