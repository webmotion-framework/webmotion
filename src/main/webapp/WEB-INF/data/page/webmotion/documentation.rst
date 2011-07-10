.. -
.. * #%L
.. * Webmotion in wiki
.. * 
.. * $Id$
.. * $HeadURL$
.. * %%
.. * Copyright (C) 2011 Debux
.. * %%
.. * This program is free software: you can redistribute it and/or modify
.. * it under the terms of the GNU Lesser General Public License as 
.. * published by the Free Software Foundation, either version 3 of the 
.. * License, or (at your option) any later version.
.. * 
.. * This program is distributed in the hope that it will be useful,
.. * but WITHOUT ANY WARRANTY; without even the implied warranty of
.. * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
.. * GNU General Lesser Public License for more details.
.. * 
.. * You should have received a copy of the GNU General Lesser Public 
.. * License along with this program.  If not, see
.. * <http://www.gnu.org/licenses/lgpl-3.0.html>.
.. * #L%
.. -
Documentation
=============

Installation
------------

Par défaut, vous pouvez utiliser WebMotion juste en important la bibliothèque dans le classpath. Il faut compiler les sources en mode debug, c'est le mode de compilation actif par défaut sous Maven.

Un fragment en Servlet 3 est utilisable pour installer sans difficultés le projet. Il sera automatiquement chargé si WebMotion se trouve dans le classpath. La servlet WebMotion sera disponible sur l'url suivante <servername>/<web-context>/deploy/.

Vous pouvez utiliser WebMotion avec Maven en ajoutant la dépendance suivante disponible sur le repository : http://debux.org/maven/repo/ : ::

 <dependency>
   <groupId>org.debux</groupId>
   <artifactId>webmotion</artifactId>
   <version>1.0</version>
 </dependency>  

Mapping
-------

Un seul fichier de configuration est nécessaire pour définir l'ensemble du mapping. Il doit se trouver dans le classpath du projet et être nommé « mapping ».

Le fichier est constitué de plusieurs sections :

 [config]
 ...
 [errors]
 ..
 [filters]
 ...
 [actions]
 ...

L'ordre des paramètres n’influe pas ni les paramètres inutiles sur la sélection de la règle. Les règles doivent être classées par ordre de priorité.

Configuration
~~~~~~~~~~~~~

La section configuration définit les packages pour récupérer les classes pour les vues, les filtres, les actions et les erreurs. Il est possible de laisser vide le nom d'un package ou de préciser WEB-INF/<package name> pour protéger les vues : ::

 package.views=org.debux.sample.views
 package.filters=org.debux.sample.filters
 package.actions=org.debux.sample.actions
 package.errors=org.debux.sample.errors

Il est aussi possible dans cette section de définir le mode de fonctionnement des actions : stateless ou statefull. Il est préférable de garder le mode par défaut, c'est-à-dire stateless. En statefull l'action sera recréée à chaque requête ce qui permet de garder des attributs dans la classe, sinon la même instance sera utilisée. ::

 mode=stateless
 mode=statefull

Un autre paramètre permet de forcer l'encodage de récupération des paramètres dans la requête HTTP. ::

 request.encoding=UTF-8

Le paramètre reloadable permet de dire si l'application est rechargeable à chaud, pour cela il faut que le code Java soit compilé avec l'option debug. En production il est conseillé de le passer à <code>false</code> et de configurer paranamer. ::

 reloadable=true
 reloadable=false

Actions
~~~~~~~

La section actions permet de définir le lien entre l'url et une méthode d'une classe. Une ligne de mapping d'action est constituée de trois parties séparées par des espaces, la méthode HTTP, l'url, et la classe avec la méthode à exécuter.

Exemple de règle qui permet de mapper l'ensemble des urls sur class avec la method : ::

 # All match
 *           /{class}/{method}                               {class}.{method}

Les paramètres peuvent être extrait soit dans le path soit dans les paramètres de la requête :

Dans le path : ::

 GET         /user/{id}                                      User.find

Dans les paramètres : ::

 GET         /user?id={id}                                   User.find

Il est possible de renommer les paramètres entre la méthode et l'url, ici l'url nous envoie une valeur mais la méthode appelée prendra comme paramètre id. ::

 GET         /user?value={id}                                User.find

Les paramètres peuvent être déclarer statiquement : ::

 *           /user/{id}?action=save                          User.save
 *           /user/{id}?action=display                       User.display

Les valeurs dans les paramètres peuvent être filtrées par un pattern. ::

 *           /{class:Action.*}/{method}                      {class}.{method}

L'ensemble des méthodes sont gérées par le framework se qui permet d'appeler des actions en fonction de la methode HTTP : ::

 GET         /user/{id}                                      User.find
 POST        /user/{id}                                      User.save
 PUT         /user/{id}                                      User.create
 DELETE      /user/{id}                                      User.delete

Il est possible de passer des paramètres par défaut, il suffit de les mettre juste après l'action, les valeurs sont séparées par des virgules : ::

 GET         /user/{id}                                      User.find            id=0

Par défaut une action correspondant à une méthode Java mais il est possible de préciser directement une vue ou une redirection ver une url. Pour cela il faut préfixer l'action par view.<extension>:<package name>.<view name> ou par url:<redirection>.

Expliciter l'action : ::

 GET         /user/{id}                                      action:User.find

Expliciter la vue : ::

 # Sur un fichier html
 GET         /index                                          view.html:Main.index
 # Sur un fichier jsp
 GET         /index                                          view.jsp:Main.index

Expliciter la redirection sur une url: ::

 # Sur un autre site web
 GET         /index                                          url:http://projects.debux.org/projects/webmotion
 # Sur une action dans le mapping
 GET         /index                                          url:/user/find

La méthode associée au mapping de l'url doit se trouver de le paquetage des contrôleurs et hériter de WebmotionAction ::

 public class User extends WebMotionAction {
     public Render find(String id) {
         return ...
     }
     ...
 }

Le framework gére les types suivants sur les méthodes :

- java.lang.BigDecimal (no default value)
- java.lang.BigInteger (no default value)
- boolean & java.lang.Boolean (default to false)
- byte & java.lang.Byte (default to zero)
- char & java.lang.Character (default to a space)
- java.lang.Class (no default value)
- double & java.lang.Double (default to zero)
- float & java.lang.Float (default to zero)
- int & java.lang.Integer (default to zero)
- long & java.lang.Long (default to zero)
- short & java.lang.Short (default to zero)
- java.lang.String (default to null)
- java.io.File (no default value)
- java.net.URL (no default value)
- java.sql.Date (no default value) (string format [yyyy-MM-dd])
- java.sql.Time (no default value) (string format [HH:mm:ss])
- java.sql.Timestamp (no default value) (string format [yyyy-MM-dd HH:mm:ss.fffffffff])
- POJO (no default value)
- java.util.Map (no default value)
- java.util.Set (no default value)
- java.util.List (no default value)
- Arrays (no default value)

Il est possible d'utiliser des sous paquetages pour les actions et les vues dans le mapping pour cela il suffit d'utiliser une notation pointée : ::

 # Sous paquet sub, classe Action et méthode index
 *           /action                                            sub.Action.index
 # Sous paquet sub/action et fichier index.jsp
 *           /view                                              view.jsp:sub.Action.index

Filtres
~~~~~~~

Il est possible de mettre en place des filtres sur des urls. La syntaxe des urls est la même que celle des filtres HTTP dans le web.xml. ::


 *           /*                                              Filters.log
 *           /test/hello/*                                   Filters.param

Un exemple d'utilisation pourrait être la vérification d'un token d'authentification sur les appels : ::

 *           /*                                              Filters.auth

Classe associée ::

 public class Filters extends WebMotionFilter {
     public void auth(String token) {
         // Before filter
         doProcess();
         // After filter
     }
 }

La méthode doProcess permet de continuer l'exécution du thread, si l'appel n'est pas fait aucune action ne sera exécutée. Il est aussi possible de renvoyer un rendu au lieu de faire le doProcess. ::

 public class Filters extends WebMotionFilter {
     public Render auth(String token) {
         if(token != null) {
             doProcess();
         } else {
             return renderView("index.html");
         }
         return null;
     }
 }

Vous pouvez accédez à l'action qui sera exécutée par le biais de la méthode de la méthode getAction, cela permet dans un filtre de modifier les paramètres d'appel.

Erreurs
~~~~~~~

Il est possible d'ajouter des actions sur les exceptions ou les codes d'erreur HTTP :

Sur exception : ::

 java.lang.NullPointerException                              Error.npe

Sur un code erreur : ::

 code:404                                                    Error.notFound

L'action se comporte comme une action classique.

Action
------

Context
~~~~~~~

Le context web reste disponible dans les actions par de biais de la méthode geContext. Le context permet de récupérer les informations sur la request et la response. En cas d'une action d'erreur vous avez accès à l'erreur par getErrorData sur le context.

Rendu
~~~~~

Plusieurs rendus disponibles dans les actions :

- **renderContent** : permet de renvoyer n'importe quel contenu en précisant le mime-type.
- **renderStream** : permet de renvoyer n'importe quel contenu de type InputStream en précisant le mime-type, pratique pour renvoyer une image dynamiquement.
- **renderView** : permet de renvoyer une vue dans le paquetage défini. Par exemple si vous disposez d'une classe Test, et comme paquetage des vues org.mon.application, la vue sera recherchée dans le répertoire /webapp/org/mon/application/test. Pour utiliser des sous-dossier, il suffit de mettre le path classiquement avec des slashs.
- **renderTemplate** : permet de renvoyer une vue sans provoquer le chargement de la page de l'utilisateur ce qui permet de faire des appels AJAX pour inclure du contenu dynamiquement. Pour utiliser des sous-dossier, il suffit de mettre le path classiquement avec des slashs.
- **renderAction** : permet de chaîner les actions un redirect est fait au niveau du client. Pour utiliser des sous paquetages, il suffit d'utiliser la notation pointée.
- **renderURL** : permet de faire une redirection.
- **renderError** : permet de renvoyer une erreur http.
- **renderXML** : permet de renvoyer un objet XML.
- **renderJSON** : permet de renvoyer un objet JSON.
- **renderJSONP** : permet de renvoyer un objet JSON par un callback Javascript.

Pour les rendus XML, JSON et JSONP, si il y a un seul objet défini dans le modèle, seule la valeur est serializée.

Il existe un rendu un peu particulier qui permet de rester sur la page sur laquelle l'utilisateur est actuellement : reloadPage.

Mise en production
------------------

Il faut enlever le mode reloadable dans le fichier de mapping, et mettre en place paranamer (http://paranamer.codehaus.org) pour qu'il génère la liste des paramètres en static.

Sous maven vous pouvez créer un profile pour cela : ::

 <profiles>        
    <profile>
        <id>prod-mode</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>com.thoughtworks.paranamer</groupId>
                    <artifactId>paranamer-maven-plugin</artifactId>
                    <version>2.3</version>
                    <executions>
                        <execution>
                            <id>run</id>
                            <configuration>
                                <sourceDirectory>${project.build.sourceDirectory}</sourceDirectory>
                                <outputDirectory>${project.build.outputDirectory}</outputDirectory>
                            </configuration>
                            <goals>
                                <goal>generate</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
 </profiles>
