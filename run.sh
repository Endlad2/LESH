#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"

if [ -f "target/LESH-1.0.1.jar" ]; then
  exec java -jar "target/LESH-1.0.1.jar"
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven не найден. Установите Maven 3.9+; также требуется JDK 21." >&2
  exit 1
fi

exec mvn clean javafx:run
