/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function showDevices(globalUrl, devices) {
 
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        crossdomain: true,
        url: globalUrl + '/listDevices?callback=?',
        success: function(data, textStatus, xmlHttp) {

            $("#wrap").tabs({
                create: function(event, ui) {
                  console.log('create tab');
                },
                activate: function(event, ui) {
                  console.log('activate tab');
                }
            });

            $('#wrap').append('<div id="page-1"><p>Welcome to Smartmeter</p></div>');
            $('#wrap').find('ul').append('<li><a href="#page-1">Home</a></li>');
 
            var tabCount = 2;

            for (var i = 0; i < data.device.length; i++) {
                // store array for later use
                devices[data.device[i].id] = data.device[i].type;
                $('#wrap').append('<div id="page-' + tabCount + '"><p>device serial id: ' + data.device[i].device + '</p>'+
                                    '<div id="chart_id-'+tabCount+'"></div>'+
                                    '<div id="table_id-'+tabCount+'"></div>'+
                                    '<div id="date_id-'+tabCount+'"></div>'+
                                    '<div id="table2_id-'+tabCount+'"></div>'+
                                    '<div id="chart2_id-'+tabCount+'"></div>'+
                                  '<div>');
                $('#wrap').find('ul').append('<li><a href="#page-' + tabCount + '">' + data.device[i].type + '</a></li>');
                tabCount++;
            }
            var selIndex = $("#wrap").tabs("option", "selected");
            $("#wrap").tabs("destroy").tabs({selected: selIndex});

         

            $("#wrap").on("tabscreate",
                function(event, ui) {
                  console.log('create tab event');
                }
            );

            $("#wrap").on("tabsactivate",
                function(event, ui) {
                  var active = $("#wrap").tabs("option", "active");
                  if ( active !== 0 ) {
                    // first tab is the home page  
                    deviceNr = active -1;
                    tabNr    = active +1;
                    //alert($("#wrap ul>li a").eq(active).attr('href'));
                    console.log('activate tab event for: '+ data.device[deviceNr].type + " id: " +data.device[deviceNr].id);
                    
                    $('#table_id').empty();
                    if (data.device[deviceNr].type === 'GAS') {
                        // show gasoverview
                        console.log('show gas overview id: '+data.device[deviceNr].id + ' page-'+tabNr);
                        showGasOverview(data.device[deviceNr].id,globalUrl,tabNr);

                        dateField = $("#wrap").find('#page-'+tabNr).find('#date_id-'+tabNr);
                        //dateField.empty(); 
                        dateField.html('<p>Date: <input type="text" id="gasdatepicker" /></p>');
                        dateField.find("#gasdatepicker").datepicker( {
                           onSelect: function(dateObject) {
                             console.log('datechanged:' + dateObject); 
                             // show gas measurement
                             showGasMeasurement(dateObject,globalUrl,tabNr);
                           }
                        });

                        dateField.find("#gasdatepicker").datepicker("option", "dateFormat", 'yy-mm-dd' );
                        dateField.find("#gasdatepicker").datepicker('setDate', new Date());
                        // show gas measurement 
                        showGasMeasurement(dateField.find("#gasdatepicker").val(),globalUrl,tabNr);
                    } else if (data.device[deviceNr].type === 'ELEC') { 
                        // show energyoverview
                        console.log('show elec overview id: '+data.device[deviceNr].id + ' page-'+tabNr);
                        showElecOverview(data.device[deviceNr].id,globalUrl,tabNr);

                        dateField = $("#wrap").find('#page-'+tabNr).find('#date_id-'+tabNr);
                        //dateField.empty(); 
                        dateField.html('<p>Date: <input type="text" id="elecdatepicker" /></p>');
                        dateField.find("#elecdatepicker").datepicker( {
                           onSelect: function(dateObject) {
                             console.log('datechanged:' + dateObject); 
                             // show gas measurement
                             showElecMeasurement(dateObject,globalUrl,tabNr);
                           }
                        });

                        dateField.find("#elecdatepicker").datepicker("option", "dateFormat", 'yy-mm-dd' );
                        dateField.find("#elecdatepicker").datepicker('setDate', new Date());
                        // show gas measurement 
                        showElecMeasurement(dateField.find("#elecdatepicker").val(),globalUrl,tabNr);
                        
                    }   

                    
                  }
                }
            );

        
        },
        error: function(request, status, error) {
            alert('Error Occured');
        }

    
    
    });



}

