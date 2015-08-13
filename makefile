GRADLE=./gradlew
PROJECT_SOURCE_NAME=vumeterlibrary

all: clean build upload

.PHONY: build
build:
	@echo 'Building ...'

	$(GRADLE) $(PROJECT_SOURCE_NAME):incrementVersionCode
	$(GRADLE) $(PROJECT_SOURCE_NAME):incrementVersionName

upload:
	@echo 'bintrayUpload ...'

	$(GRADLE) $(PROJECT_SOURCE_NAME):bintrayUpload

localbuild:

	@$(MAKE) build

clean:
	@echo 'Cleaning ...'
	$(GRADLE) clean
	@echo 'Clean Successful'
