.PHONY: dump release cljs

SHELL=bash
CLJ=$(shell type -p clojure;)

release:
	# CLEAN
	rm -rf .cpcache resources/public/js/app target pom.xml; 
	# BUILD
	$(CLJ) -A:cljs release app; $(CLJ) -A:release
	# UPLOAD
	# scp target/simple-0.1.0-standalone.jar user@ip:path
	# DONE!

cljs:
	$(CLJ) -A:cljs watch app
