KBuildMiner
====

setup run.sh by executing `mvn dependency:build-classpath -Dmdep.outputFile=.classpath-scala`

then run with `./run.sh gsd.buildanalysis.linux.KBuildMinerMain <parameters>`


Rewrote the frontend that it no longer depends on naming magic of folders.
Also removed manual patching of results with fixed resources. This should be handled
separately.



A list of root folders/files can now be provided as parameter (comma separated list). Here are the previous defaults

Linux: "[x86Makefile],block,crypto,drivers,firmware,fs,init,ipc,kernel,lib,mm,net,security,sound"

Busybox: "applets,archival,archival/libarchive,console-tools,coreutils,coreutils/libcoreutils,debianutils,e2fsprogs,editors,findutils,init,libbb,libpwdgrp,loginutils,mailutils,miscutils,modutils,networking,networking/libiproute,networking/udhcp,printutils,procps,runit,selinux,shell,sysklogd,util-linux,util-linux/volume_id"


The arch/xxx/Makefile is no longer handled in a special way. Instead it should be copied to the root of the project
before executing the analysis normally.