# MiniPascal Compiler

Este proyecto es un compilador para el lenguaje MiniPascal, desarrollado en Java utilizando ANTLR para el análisis léxico y sintáctico. Incluye una interfaz gráfica de usuario (GUI) para facilitar la interacción con el compilador.

## Estructura del Proyecto

```
C:.
├───.idea
│   └───codeStyles
├───src
│   ├───main
│   │   ├───antlr4
│   │   └───java
│   │       └───org
│   │           └───main
│   └───test
│       └───java
└───target
    ├───classes
    │   └───org
    │       └───main
    └───test-classes
```

## Requisitos

- **Java 8 o superior**: Asegúrate de tener instalado Java en tu sistema.
- **Maven**: Este proyecto utiliza Maven para la gestión de dependencias.

## Instalación

1. Clona este repositorio:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd MiniPascalCompiler
   ```

2. Compila el proyecto utilizando Maven:
   ```bash
   mvn clean install
   ```

3. Ejecuta el proyecto:
   ```bash
   java -cp target/classes org.main.Main
   ```

## Uso

1. Al iniciar la aplicación, se abrirá la interfaz gráfica del compilador.
2. Haz clic en el botón **"Abrir Archivo"** para seleccionar un archivo MiniPascal (`.txt`).
3. Haz clic en el botón **"Compilar"** para analizar el archivo seleccionado.
4. Los errores de compilación se mostrarán en el área de salida, y las líneas con errores se resaltarán.

## Características

- **Interfaz gráfica moderna**: Incluye un diseño atractivo con gradientes y botones personalizados.
- **Resaltado de errores**: Identifica y resalta las líneas con errores en el código fuente.
- **Animaciones**: Mensajes animados para indicar el éxito de la compilación.
- **Soporte para ANTLR**: Utiliza ANTLR para el análisis léxico y sintáctico.

## Archivos Clave

- **[src/main/antlr4/MiniPascal.g4](src/main/antlr4/MiniPascal.g4)**: Gramática ANTLR para MiniPascal.
- **[src/main/java/org/main/Main.java](src/main/java/org/main/Main.java)**: Clase principal que inicia la aplicación.
- **[src/main/java/org/main/CompilerGUI.java](src/main/java/org/main/CompilerGUI.java)**: Implementación de la interfaz gráfica.

## Pruebas

Los archivos de prueba se encuentran en el directorio `src/test/java/`:
- **E_MultDecl - ERROR.txt**: Archivo con errores de declaración múltiple.
- **E_MultDecl.txt**: Archivo sin errores.

