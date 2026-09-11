# GlobalDocs Solutions — Patrones de Diseño

Este proyecto es el Sistema de Procesamiento de Documentos Especiales de GlobalDocs Solutions,
una compañía multinacional que procesa más de 50.000 documentos diarios (facturas electrónicas,
contratos legales, reportes financieros, certificados digitales y declaraciones tributarias),
donde cada país tiene sus propias reglas sobre qué documentos acepta, qué tamaño permite y cómo
valida el identificador fiscal del emisor. A continuación se explican los tres patrones
creacionales que sostienen la arquitectura del backend.

## Factory Method

Existen cinco tipos de documento y cada uno necesita una lógica de procesamiento distinta: una
factura electrónica extrae su número y lo encola para reportarlo a la autoridad tributaria, un
certificado digital verifica su integridad, una declaración tributaria registra el identificador
fiscal del declarante, y así con cada tipo. En vez de tener un único método con un gran `switch`
que decida qué hacer según el tipo de documento —algo difícil de mantener y fácil de romper cada
vez que se agrega un tipo nuevo—, el proyecto define una clase creadora abstracta
(`DocumentProcessorFactory`) que declara el método fábrica `createProcessor()`, y una subclase
concreta por cada tipo de documento (`ElectronicInvoiceProcessorFactory`,
`LegalContractProcessorFactory`, `FinancialReportProcessorFactory`,
`DigitalCertificateProcessorFactory`, `TaxReturnProcessorFactory`) que decide qué procesador
concreto crear. El resto del sistema nunca instancia un procesador directamente ni pregunta qué
tipo de documento es: simplemente pide la fábrica correspondiente y le delega el trabajo. Esto
permite que GlobalDocs agregue un sexto tipo de documento en el futuro sin modificar ninguna clase
existente, solo agregando una fábrica y un procesador nuevos.

## Abstract Factory

La variación por país es distinta a la variación por tipo de documento: no es "un objeto
distinto", sino un conjunto de reglas que deben ser coherentes entre sí. Cada país necesita, a la
vez, un validador de identificación fiscal con el formato correcto (NIT en Colombia, RFC en
México, CNPJ en Brasil, NIF en España, EIN en Estados Unidos) y un conjunto de reglas de documento
que indique qué tipos acepta ese país y cuál es el tamaño máximo permitido. Si estos dos objetos se
construyeran por separado, sería posible —por un simple error de programación— validar el
identificador fiscal de un país contra las reglas de documento de otro, algo inaceptable en un
sistema de cumplimiento normativo multinacional. Por eso el proyecto usa una fábrica abstracta
(`CountryComplianceFactory`) que crea toda la familia de productos relacionados de un mismo país
—el validador de identificación fiscal y las reglas de documento— y una implementación concreta
por país (`ColombiaComplianceFactory`, `MexicoComplianceFactory`, `BrazilComplianceFactory`,
`SpainComplianceFactory`, `UnitedStatesComplianceFactory`) que garantiza que ambos productos
siempre viajan juntos y nunca se mezclan entre países.

## Singleton

Las fábricas anteriores no tienen estado propio que cambie con cada solicitud: registrar qué
fábrica atiende qué tipo de documento, o qué fábrica atiende qué país, es un trabajo que debe
hacerse una sola vez, no en cada documento que se sube. Con miles de documentos procesándose por
minuto, crear e indexar estas fábricas en cada solicitud sería trabajo repetido e innecesario, y
además abriría la puerta a que distintas partes del sistema usaran registros inconsistentes de las
mismas fábricas. Por eso ambos registros —`DocumentProcessorFactoryProvider`, que resuelve la
fábrica de procesamiento según el tipo de documento, y `CountryComplianceRegistry`, que resuelve
la fábrica de cumplimiento según el país— se implementan como Singleton: su constructor es
privado, se construyen una sola vez con todas las fábricas ya registradas, y toda la aplicación
accede a la misma instancia a través de un único método `getInstance()`. Esto garantiza que
cualquier solicitud, sea de un documento individual o de un lote de miles, consulte siempre el
mismo estado compartido en memoria.

## Cómo se complementan

En una sola solicitud de procesamiento, primero el Singleton `CountryComplianceRegistry` ubica la
Abstract Factory del país correspondiente, que entrega el validador de identificación fiscal y las
reglas de documento de ese país; si el documento pasa esa validación, el Singleton
`DocumentProcessorFactoryProvider` ubica el Factory Method del tipo de documento correspondiente,
que finalmente crea el procesador y ejecuta el procesamiento real. Cada patrón resuelve una
responsabilidad distinta —dónde buscar, qué familia de reglas de país aplicar, y qué procesador de
tipo de documento ejecutar— y juntos evitan que el sistema termine lleno de condicionales y
creación de objetos dispersos, algo insostenible con 50.000 documentos diarios y regulaciones que
cambian por país.
