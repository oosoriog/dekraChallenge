APP_SERVICE=app
MVNW=./mvnw

ifeq ($(OS),Windows_NT)
	MVNW=mvnw.cmd
endif

.PHONY: help clean test verify build up down restart logs ci deploy sonar

help:
	@echo "Targets:"
	@echo "  make test       Run tests"
	@echo "  make verify     Run full Maven verification"
	@echo "  make build      Package app skipping tests"
	@echo "  make up         Build and start Docker Compose"
	@echo "  make down       Stop Docker Compose"
	@echo "  make restart    Restart Docker Compose"
	@echo "  make logs       Follow app logs"
	@echo "  make ci         Local quality gate"
	@echo "  make deploy     Run ci and deploy Docker"
	@echo "  make sonar      Run Sonar analysis"

clean:
	$(MVNW) clean

test:
	$(MVNW) -B -ntp test

verify:
	$(MVNW) -B -ntp clean verify

build:
	$(MVNW) -B -ntp clean package -DskipTests

up:
	docker compose up --build -d
	docker compose ps

down:
	docker compose down --remove-orphans

restart: down up

logs:
	docker compose logs -f $(APP_SERVICE)

ci:
	$(MVNW) -B -ntp clean verify

deploy: ci down up

sonar:
	$(MVNW) -B -ntp sonar:sonar
