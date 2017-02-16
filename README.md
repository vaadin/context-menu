# Vaadin ContextMenu Add-on 

Vaadin ContextMenu is an UI component add-on for [Vaadin Framework](https://github.com/vaadin/framework).

## Online demo

Try the add-on demo at http://demo.vaadin.com/context-menu-demo/

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/addon/vaadin-contextmenu

## Building and running demo

    git clone https://github.com/vaadin/context-menu.git
    cd context-menu
    mvn install
    cd vaadin-context-menu-demo
    mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Release notes

### Version 0.7 - Latest version for Vaadin Framework 7
 - Basic support for component context menu everywhere
 - Basic code examples
 - Use ContextClickEvent API introduced in Vaadin 7.6
 
### Version 2.0 - Latest version for Vaadin Framework 8
 - Support for Vaadin Framework 8
 - Renamed the groupId to `com.vaadin` from `com.vaadin.addon
 - Renamed the packaging to `com.vaadin.contextmenu` from `com.vaadin.addon.contextmenu`
 - Renamed widgetset to `com.vaadin.contextmenu.WidgetSet` from `com.vaadin.addon.contextmenu.WidgetSet`

### Future plans
- Unify ContextMenu with MenuBar API
- Integrate into framework

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such.
Please contact juha@vaadin.com

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.

ContextMenu is written by Vaadin Ltd.

# Developer Guide

## Getting started

Check a demo application included.

