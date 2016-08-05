# recadastramento-android
Sistema de recadastramento de imóveis

O aplicativo de Recadastramento desenvolvido na plataforma Android, tem como finalidade a atualização cadastral de imóveis e clientes através da coleta de informações diretamente em campo. Os dados são carregados no aplicativo via arquivos de texto separados por rota e após a atualização dessas informações junto ao cliente, são transmitidos para o Gsan (link).

*Com o aplicativo é possível atualizar os seguintes dados cadastrais:*

* Imóvel: endereço e dados de localização; categoria, subcategoria e economias; ramo de atividade; dados de ligação de água e esgoto; dados do hidrômetro; e fotos;
* Cliente: nome; endereço; contato; CPF e RG; e informações complementares.


Geração do Instalador (.apk)
===
Para gerar o arquivo .apk via ADT instalado no Eclipse, clique com o botão direito no projeto ``recadastramento-android > Android Tools > Exportar aplicativo assinado``
Em seguida, deve ser selecionada a chave para a IDE assinar a aplicação, que se encontra na pasta ``/.android`` do usuário. Por último, informar a senha ``android`` e finalizar.

**Tecnologias Utilizadas:**

* [Android na versão mínima 2.3](http://developer.android.com/index.html)
* [Java 1.7](https://github.com/prodigasistemas/gsan/wiki/Instala%C3%A7%C3%A3o-do-Java)


Instalação do Aplicativo
===

Inicialmente, deve ser criada a pasta ``external_sd`` com a seguinte estrutura de pastas na memória interna ou cartão de memória do celular/tablet:

* ``Cadastro``
  * ``Log``
  * ``Retorno``
  * ``Roteiros``
  * ``Versões``

Após isso, basta fazer o download do instalador (.apk), mover para a pasta ``Versões`` e instalar o aplicativo.
