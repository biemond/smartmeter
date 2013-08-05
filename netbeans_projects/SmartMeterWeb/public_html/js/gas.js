/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function showGasOverview(val, globalUrl, tabNr) {
            
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listGasOverview/' + val + '?callback=?',
        success: function(json, textStatus, xmlHttp) {

          tableId = 'table_id-'+tabNr;
          pageId  = 'page-'+tabNr;
          chartId = 'chart_id-'+tabNr;
          console.log(pageId + " "+tableId+ " "+chartId);
           

            
            function map(el) {
                return [[el.date.substring(0, 10), el.consumption, el.difference]];
            }
            ;
            result = $.map(json.gasOverview, map);
 
            function map2(el) {
                return [[ el.difference]];
            }
            ;
            result2 = $.map(json.gasOverview, map2);            
            chartField = $("#wrap").find('#'+pageId).find('#'+chartId);
            chartField.sparkline(result2, {type: 'bar', 
                                           disableHiddenCheck: true,
                                           height: '100px',
                                           tooltipSuffix: ' gas difference in m3',
                                           colorMap: { '0:2': 'blue',
                                                       '3:': 'red' }});
 
 
         
            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="pretty" id="dataGas"></table>');

            tableField.find('#dataGas').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "aaSorting": [[1, 'desc']],
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

 

function showGasMeasurement(date, globalUrl, tabNr) {

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listGas/' + date + '?callback=?',
        success: function(json, textStatus, xmlHttp) { 
            
        tableId = 'table2_id-'+tabNr;
        pageId  = 'page-'+tabNr;  
        chartId = 'chart2_id-'+tabNr;
        console.log(pageId + " "+tableId+ " "+chartId);
            
          function map(el) {
                return [[el.time.substring(0, 16), el.measurement, el.enabled]]
          };
          if ( json !== null) {    
            result = $.map(json.gasMeasurement, map);
            
            
            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="pretty" id="gasdata2"></table>');
            tableField.find('#gasdata2').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "aaSorting": [[1, 'desc']],
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


