jax-ws-osgi
===========

JAX-WS support for OSGi services using Apache CXF




portal-ext.properties
---------------------
```
#
# Equinox OSGi debug levels for ResolverImpl
#
org.eclipse.osgi/resolver/debug=true
org.eclipse.osgi/resolver/wiring=true
org.eclipse.osgi/resolver/imports=true
org.eclipse.osgi/resolver/requires=true
org.eclipse.osgi/resolver/generics=true
org.eclipse.osgi/resolver/uses=true
org.eclipse.osgi/resolver/cycles=true

#
# OSGi telnet config
# see https://www.liferay.com/web/raymond.auge/blog/-/blogs/liferay-osgi-and-shell-access-via-gogo-shell
#
extra.bundles.1=file:/opt/liferay.downloads/lib/osgi
module.framework.initial.bundles=\
    felix-fileinstall.jar@start,\
    ${extra.bundles.1}/org.apache.felix.gogo.command-0.12.0.jar@start,\
    ${extra.bundles.1}/org.apache.felix.gogo.runtime-0.10.0.jar@start,\
    ${extra.bundles.1}/org.apache.felix.gogo.shell-0.10.0.jar@start,\
    ${extra.bundles.1}/org.eclipse.equinox.console_1.0.0.v20120522-1841.jar@start
module.framework.properties.osgi.console=11311
```

system-ext.properties
---------------------
```
#
# Set Equinox OSGi options file
#
osgi.debug=/opt/liferay.git/bundles/portal-ext.properties
```
