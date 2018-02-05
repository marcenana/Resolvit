Instalacion usada:
jdk8: jdk-8u161-windows-x64.exe
Eclipse Oxygen.2 Release (4.7.2)

Dos librerias:
Gson para hacer el JSON
Snowball-stemmer para encontrar la raiz. No me parece muy eficaz (por ejemplo no reconoce la raiz de an -> a / is -> be) pero no tenia modo de volver a buscar otra (sin conexion internet)

Les explico como usar el codigo:  
- Se configura con el fichero file.properties
     El documento a escanear tiene que estar configurado en ese fichero. En el ejemplo fileName, el texto esta en un fichero testIndex1.txt sobre el disco C.
     Las palabras que no tienen que ser incluidas. excludeWords= las palabras separadas solo por coma
     La libreria que elegi permite encontrar la raiz en varias lenguas. StemLang
- La clase Objeto principal WordOccurenceSentence
- La clase IndexTheText
	 
Elecciones proprias:
Agregé en mi Objecto WordOccurenceSentence
un atributo stem por si queremos ver la raiz (no incluido en el JSON, hay que sacar transient al atributo si se quiere. La verdad es que la biblioteca solo da la raiz, por lo cual el Stem muchas veces no tiene real sentido)
una lista para word para escribir las diferentes maneras encontradas en el texto de la misma raiz
-> Stem no lo puse en la salida Json, pero me parecio que las diferentes palabras aportaban algo. El resultado es diferente del pedido
Utilizé setPrettyPrinting de Json para que sea lisible como lo pedido, pero no queda exactamente ya que los tableros estan en varias lineas.  El resultado es diferente del pedido


