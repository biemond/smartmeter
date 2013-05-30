/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function showGasOverview(val, globalUrl, tableId,pageId) {
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listGasOverview/' + val + '?callback=?',
        success: function(json, textStatus, xmlHttp) {
            function map(el) {
                return [[el.date.substring(0, 10), el.consumption, el.difference]]
            }
            ;
            result = $.map(json.gasOverview, map);

            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="bordered-table zebra-striped" id="dataGas"></table>');

            tableField.find('#dataGas').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "aoColumns": [
                    {"sTitle": "date"},
                    {"sTitle": "consumption"},
                    {"sTitle": "difference"}
                ]
            });

        },
        error: function(request, status, error) {
            alert('Error Occured');
        }


    });
}

function showGasMeasurement(date, globalUrl, tableId,pageId) {

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listGas/' + date + '?callback=?',
        success: function(json, textStatus, xmlHttp) {
            function map(el) {
                return [[el.time.substring(0, 16), el.measurement, el.enabled]]
            };
          if ( json !== null) {    
            result = $.map(json.gasMeasurement, map);
            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="bordered-table zebra-striped" id="gasdata2"></table>');
            tableField.find('#gasdata2').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "aoColumns": [
                    {"sTitle": "time"},
                    {"sTitle": "measurement"},
                    {"sTitle": "enabled"}
                ]
            });
          }
        },
        error: function(request, status, error) {
            alert('Error Occured');
        }


    });
}


