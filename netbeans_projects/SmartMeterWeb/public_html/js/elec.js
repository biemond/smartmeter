/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function showElecOverview(val, globalUrl, tabNr) {
    
    
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listEnergyOverview/' + val + '?callback=?',
        success: function(json, textStatus, xmlHttp) {
           tableId = 'table_id-'+tabNr;
           pageId  = 'page-'+tabNr;
           chartId = 'chart_id-'+tabNr;
           console.log(pageId + " "+tableId+ " "+chartId);

            
            function map(el) {
                return [[el.date.substring(0, 10), el.consumption, el.consDifference, el.production, el.prodDifference]];
            };
            
            function map2(el) {
                return [[ el.consDifference, -1 * el.prodDifference]];
            };
            result2 = $.map(json.energyOverview, map2);            
            chartField = $("#wrap").find('#'+pageId).find('#'+chartId);
            chartField.sparkline(result2, {type: 'bar', 
                                           disableHiddenCheck: true,
                                           height: '150px',
                                           tooltipSuffix: ' energy difference in KWH',
                                           stackedBarColor: ['red','blue']
                                           });

            
            result = $.map(json.energyOverview, map);
            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="pretty" id="elecdata"></table>');
            tableField.find('#elecdata').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "aaSorting": [[1, 'desc']],
                "aoColumns": [
                    {"sTitle": "date"},
                    {"sTitle": "consumption"},
                    {"sTitle": "difference"},
                    {"sTitle": "production"},
                    {"sTitle": "difference"}
                ]              
            });
        },
        error: function(request, status, error) {
            alert('Error Occured');
        }
    });

}

function showElecMeasurement(date, globalUrl, tabNr) {



    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listEnergy/' + date + '?callback=?',
        success: function(json, textStatus, xmlHttp) {
           tableId = 'table2_id-'+tabNr;
           pageId  = 'page-'+tabNr;  
           chartId = 'chart2_id-'+tabNr;
           console.log(pageId + " "+tableId+ " "+chartId);
    
            function map(el) {
                return [[el.time.substring(0, 16), el.meter181,el.meter182,el.meter281,el.meter282, el.currentConsumption,el.currentProduction]];
            };


          if ( json !== null) {  
            result = $.map(json.energyMeasurement, map);
            tableField = $("#wrap").find('#'+pageId).find('#'+tableId);
            tableField.html('<table cellpadding="0" cellspacing="0" border="0" class="pretty" id="elecdata2"></table>');
            tableField.find('#elecdata2').dataTable({
                "sDom": "<'row'<'span8'l><'span8'f>r>t<'row'<'span8'i><'span8'p>>",
                "aaData": result,
                "bAutoWidth" : false,
                "aaSorting": [[1, 'desc']],
                "aoColumns": [
                    {"sTitle": "time"       ,"sWidth": "30%" },
                    {"sTitle": "181"        ,"sWidth": "15%"},
                    {"sTitle": "182"        ,"sWidth": "15%"},
                    {"sTitle": "281"        ,"sWidth": "15%"},
                    {"sTitle": "282"        ,"sWidth": "15%"},
                    {"sTitle": "consumption","sWidth": "20%"},
                    {"sTitle": "production" ,"sWidth": "20%"},
                ]
                
                
            });
          }
        },
        error: function(request, status, error) {
            alert('Error Occured');
        }


    });
}

