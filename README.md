Instrucciones de ejecución:

1. Levantar docker. Habrán 5 contenedores: base de datos, backend, bff-web, bff-mobile, bff-cajero.
2. Al backend le corresponde el puerto 8080, bff-web: 8081, bff-mobile: 8082, bff-cajero: 8083
3. La base de datos viene ya con datos cargados desde el backend.
4. Desde Postman, aplicar los métodos GET (estados-financieros, intereses, movimientos-financieros), para cada bff en el puerto que le corresponda. Para ello,
   deberá autenticarse según usuario y contraseña:
   bff-web: webuser/web123
   bff-mobile: mobileuser/mobile123
   bff-cajero: cajerouser/cajero123
6. Los métodos GET posibles para aplicar en Postman son:
   estados-financieros/{id}
   estados-financieros/cuenta/{cuentaId}
   estados-financieros/todos
   intereses/{id}
   intereses/cuenta/{cuentaId}
   intereses/todos
   movimientos-financieros/{id}
   movimientos-financieros/todos
7. Comparar los resultados obtenidos para cada bff. Los métodos GET entregan más o menos datos dependiendo del bff en cuestión.
