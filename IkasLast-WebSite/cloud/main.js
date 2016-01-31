
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function (request, response) {
    response.success("Hello world!");
});
Parse.Cloud.define("datos", function (request, response) {
    var query = new Parse.Query("FitRegistro");
    query.equalTo("Usuario", request.params.user);
    query.find({
        success: function (results) {

            response.success(results);
        },
        error: function () {
            response.error("movie lookup failed");
        }
    });
});
Parse.Cloud.define("grafico", function (request, response) {
    var query = new Parse.Query("FitRegistro");
    query.equalTo("Usuario", request.params.user);
    query.equalTo("Grupo", request.params.grupo);
    query.find({
        success: function (results) {

            response.success(results);
        },
        error: function () {
            response.error("error recuperando datos de grafico");
        }
    });
});
Parse.Cloud.define("gruposFecha", function (request, response) {
    var query = new Parse.Query("FitRegistro");
    query.equalTo("FechaReg", request.params.fecha);
    query.equalTo("Grupo", request.params.grupo);
    query.find({
        success: function (results) {

            response.success(results);
        },
        error: function () {
            response.error("error recuperando datos grupos fecha");
        }
    });
});
Parse.Cloud.define("datosUsuario", function (request, response) {
    var query = new Parse.Query("Usuarios");
    query.equalTo("NomUsuario", request.params.usuario);

    query.find({
        success: function (results) {

            response.success(results);
        },
        error: function () {
            response.error("error recuperando datos Usuario");
        }
    });
});
Parse.Cloud.define("aliasUsuario", function (request, response) {
    var query = new Parse.Query("Usuarios");
    query.equalTo("Alias", request.params.alias);

    query.find({
        success: function (results) {

            if ((results.length) > 0) {
                var result = true;
            } else {
                var result = false;
            }

            response.success(result);
        },
        error: function () {
            response.error("error recuperando datos Usuario");
        }
    });
});
Parse.Cloud.define("grupos", function (request, response) {
    var query = new Parse.Query("Grupos");

    query.find({
        success: function (results) {

            response.success(results);
        },
        error: function () {
            response.error("error recuperando datos grupos ");
        }
    });
});
Parse.Cloud.define("grupos_datos", function (request, response) {
    var query = new Parse.Query("Grupos");
    var query2 = new Parse.Query("FitRegistro");
    query.find({
        success: function (results) {

            for (var i = 0; i < results.length; i++) {
                query2.equalTo("Grupo", results[i].get("NombreGrupo"));
                var sum = 0;
                query2.find({
                    success: function (results) {

                        for (var i = 0; i < results.length; ++i) {
                            sum += results[i].get("Distancia");
                        }

                    },
                    error: function () {
                        response.error("error recuperando datos grupos ");
                    }
                });
                var datos_grupos = {
                    nombreGrupo: results[i].get("NombreGrupo"),
                    metros: sum
                };

            }

            response.success(datos_grupos);
        },
        error: function () {
            response.error("error recuperando datos grupos ");
        }
    });
});
Parse.Cloud.define("gruposDistancia", function (request, response) {
    var query = new Parse.Query("FitRegistro");
    var grupo = request.params.grupo;

    query.equalTo("Grupo", grupo);
    query.find({
        success: function (results) {


            var sum = 0;

            for (var i = 0; i < results.length; i++) {
                sum += results[i].get('Distancia');

            }
            var data = {x: grupo, y: sum};

            response.success(data);
        },
        error: function () {
            response.error("error recuperando datos grupos fecha");
        }
    });
});
Parse.Cloud.define("usuariosDistancia", function (request, response) {

    var query = new Parse.Query("FitRegistro");
    var arrayUsuarios = new Array();
    var arrayUsuariosGrupo = new Array();
    var arrayDistancias = new Array();
    var arraySegundos = new Array();
    var grupo = request.params.grupo;
    query.equalTo("Grupo", grupo);
    query.find({
        success: function (results) {
            // alert(results.length);
            if (results.length !== 0) {
                var sum = 0;
                var usuario;
                var fecha;
                var fechasArray = new Array();
                var segundos;
                var arrayFechas = new Array();
                for (var i = 0; i < results.length; i++) {

                    sum = results[i].get('Distancia');
                    usuario = results[i].get('Alias');
                    fecha = results[i].get('FechaReg');
                    segundos = results[i].get('Segundos');

                    var existe = false;

                    for (var x = 0; x < arrayUsuariosGrupo.length; x++) {
                        if (arrayUsuariosGrupo[x].x == usuario) {
                            existe = true;
                            break;
                        }
                    }
                    if (!existe) {
                        arrayUsuariosGrupo.push({x: usuario, y: [sum], time: [segundos], fechas: [fecha]});

                    } else {
                        for (var j = 0; j < arrayUsuariosGrupo.length; j++) {
                            if (arrayUsuariosGrupo[j].x == usuario) {
                                indice = j;
                                break;
                            }
                        }
                        arrayUsuariosGrupo[indice].y.push(sum);
                        arrayUsuariosGrupo[indice].time.push(segundos);
                        arrayUsuariosGrupo[indice].fechas.push(fecha);


                    }
                }


                response.success(arrayUsuariosGrupo);
            } else {
                response.success("sin usuarios");
            }
        },
        error: function () {
            response.error("error en usuariosDistancia!!!!");
        }
    });
});