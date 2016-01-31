/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var arrayUsuariosGrupo = new Array();
var grupoPasado = 'ClaseJ';
var pUG = new Array();
var contUG = 0;
function test(grupo) {
    var query = new Parse.Query("FitRegistro");
    var arrayUsuarios = new Array();
    var arrayUsuariosGrupo = new Array();
    var arrayDistancias = new Array();
    var arraySegundos = new Array();
    // var grupo = request.params.grupo;
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
                    //var data = {x: usuario, distancia: sum};
                    // var indice = arrayUsuarios.indexOf(usuario);
                    var existe=false;
                    
                    for (var x = 0; x < arrayUsuariosGrupo.length; x++) {
                        if (arrayUsuariosGrupo[x].x == usuario) {
                            existe=true;
                            break;
                        }
                    }
                    if (!existe) {
                        arrayUsuariosGrupo.push({x: usuario, y: [sum], time: [segundos], fechas: [fecha]});
                        // arrayUsuariosGrupo.push({x: usuario, y: sum, time: segundos ,fechas: [fecha]});

                        // arrayUsuarios.push(usuario);
                        // arrayDistancias.push(sum);
                        // arraySegundos.push(segundos);
                        // arrayFechas.push(fecha);
                    } else {
                        for (var j = 0; j < arrayUsuariosGrupo.length; j++) {
                            if (arrayUsuariosGrupo[j].x == usuario) {
                                indice = j;
                                break;
                            }
                        }
                        
                     
                        // arrayFechas.push(fecha);
                        //arrayDistancias[indice] += sum;
                        //arraySegundos[indice] += segundos;
                    }
                }
                /*for (var i = 0; i < arrayUsuarios.length; i++) {
                 var data = {x: arrayUsuarios[i], y: arrayDistancias[i], tiempo: arraySegundos[i], fechas: arrayFechas};
                 arrayUsuariosGrupo.push(data);
                 }*/

                response.success(arrayUsuariosGrupo);
            } else {
                response.error("sin usuarios");
            }
        },
        error: function () {
            response.error("error en usuariosDistancia!!!!");
        }
    });
}
function m() {

    obtenerUsersGrupo(grupoPasado);
}
function obtenerUsersGrupo(grupoPasado) {
    var promiseG = Parse.Cloud.run('usuariosDistancia', {grupo: grupoPasado}).then(function (results) {

        contUG = contUG + 1;
        arrayUsuariosGrupo.push(results);



    }).then(function () {




        if (contUG === pUG.length) {
            alert('ahora con usuarios?');
            //mostrar();
        }
    });
    pUG.push(promiseG);
    // return Promise.all(promises);


}

function obtenerUsuariosGrupo(grupoPasado) {
    var arrayUsuarios = new Array();
    var arrayDistancias = new Array();
    var sum;
    var usuario;
    var query = new Parse.Query("FitRegistro");
    query.equalTo("Grupo", grupoPasado);
    query.find({
        success: function (results) {
            // alert(results.length);

            for (var i = 0; i < results.length; i++) {

                sum = results[i].get('Distancia');
                usuario = results[i].get('Alias');
                //var data = {x: usuario, distancia: sum};
                var indice = arrayUsuarios.indexOf(usuario);
                if (indice === -1) {
                    arrayUsuarios.push(usuario);
                    arrayDistancias.push(sum);


                } else {

                    arrayDistancias[indice] += sum;
                }
            }
            for (var i = 0; i < arrayUsuarios.length; i++) {
                var data = {x: arrayUsuarios[i], y: arrayDistancias[i]};
                arrayUsuariosGrupo.push(data);
            }

            alert(arrayUsuariosGrupo.length)




        },
        error: function () {

        }
    });
    /*  pg = Parse.Cloud.run('grupos').then(function (ratings) {
     
     // return ratings;
     contGrupos++;
     var tablaGrupo = '<tr class="active">';
     for (var i = 0; i < ratings.length; i++) {
     // alert(ratings);
     grupo = ratings[i].get("NombreGrupo");
     
     //tablaGrupo += '<tr class="danger"><td class="active"><a onclick="fechas_grupos(' + grupo + ');" href="#portfolio"> ' + grupo + '</a>';
     arrayGrupos.push(grupo);
     //  alert(arrayGrupos[i])
     tablaGrupo += '<tr class="danger"><td class="active"><a  onClick="grupos_distancia();"  href="#portfolio" > ' + grupo + '</a>';
     // $('#grupo').click(function(){ alert(grupo); return false; });
     tablaGrupo += '</td><tr>';
     }
     tablaGrupo += '</tr>';
     document.getElementById('resultadosGrupos').innerHTML = tablaGrupo;
     }).then(function () {
     if (contGrupos === pgs.length) {
     grupos_distancia();
     }
     });
     pgs.push(pg);*/
}