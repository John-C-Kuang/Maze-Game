all: clean jar

clean:
	rm -rf build
	rm -rf .gradle

jar:
	./gradlew clean build