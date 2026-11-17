#!/usr/bin/env bash
# Builds plugin-dist/example-ca-plugin.zip containing the plugin jar, plugin-meta.json,
# and the connector + profile configuration; prints the SHA-256 for upload to TLM.
set -euo pipefail
cd "$(dirname "$0")"
OUT=plugin-dist; BUILD=build
rm -rf "$OUT" "$BUILD"; mkdir -p "$OUT" "$BUILD/classes"
javac --release 17 -d "$BUILD/classes" \
  $(find ../sdk-contract/src src -name '*.java')
printf 'Main-Class: com.example.ca.MyCaPluginRunner\n' > "$BUILD/manifest.txt"
jar --create --file "$BUILD/example-ca-plugin.jar" --manifest "$BUILD/manifest.txt" -C "$BUILD/classes" .
cat > "$BUILD/plugin-meta.json" <<JSON
{ "pluginType": "certificate_authority", "name": "Example CA", "version": "1.0.0",
  "jar": "example-ca-plugin.jar", "mainClass": "com.example.ca.MyCaPluginRunner" }
JSON
cp config/connector-configuration.yaml config/profile-template.yaml "$BUILD/"
(cd "$BUILD" && zip -q -j "../$OUT/example-ca-plugin.zip" example-ca-plugin.jar plugin-meta.json \
   connector-configuration.yaml profile-template.yaml)
sha256sum "$OUT/example-ca-plugin.zip"
