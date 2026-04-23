curl -v -X POST http://127.0.0.1:2000/api/v2/packages \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"language": "python", "version": "3.9.1"}'

curl -v -X POST http://127.0.0.1:2000/api/v2/packages \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"language": "java", "version": "15.0.2"}'
