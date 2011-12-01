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
Wikimotion
==========

Wikimotion est un wiki basé sur WebMotion. Il permet d'utiliser plusieurs types de syntaxes (HTML, RST et LaTex).

- Example HTML_
- Example RST_
- Example LaTex_

.. _html: /wikimotion/deploy/display/wikimotion/example_html
.. _RST: /wikimotion/deploy/display/wikimotion/example_rst
.. _LaTex: /wikimotion/deploy/display/wikimotion/example_tex

Ce site est fait avec WebMotion et WikiMotion.

Installation
------------

Il suffit de récupérer le war disponible à l'adresse suivante http://projects.debux.org/projects/webmotion/files et de le déposer dans le webapps d'un tomcat.

Pour pouvoir utiliser la génération des pages en RST et en Latex, veuillez suivre les instructions suivantes.

Installation LaTex
~~~~~~~~~~~~~~~~~~

Il faut installer le paquet tth ainsi :

::

 apt-get install tth

Installation RST
~~~~~~~~~~~~~~~~

Il faut installer les paquets suivants :

::

 apt-get install python-docutils python-pygments


Après il faut installer le script rst2html-pygments

::

 wget http://docutils.sourceforge.net/sandbox/code-block-directive/tools/pygments-enhanced-front-ends/rst2html-pygments
 cp rst2html-pygments /usr/bin/rst2html-pygments
 chmod 755 /usr/bin/rst2html-pygments

Pour plus de renseignement, vous pouvez consulter le site suivant http://docutils.sourceforge.net/sandbox/code-block-directive/docs/syntax-highlight.html.
