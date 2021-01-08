.PHONY: dump release cljs

SHELL=bash
CLJ=$(shell type -p clojure;)

release:
	# CLEAN
	rm -rf .cpcache resources/public/js/app target pom.xml; 
	# BUILD
	$(CLJ) -A:dev:cljs; $(CLJ) -A:release
	# UPLOAD
	# scp target/simple-0.1.0-standalone.jar user@ip:path
	# DONE!
	sudo java -jar target/basic_clj_web_app-0.1.0-standalone.jar
cljs:
	$(CLJ) -A:cljs watch app
