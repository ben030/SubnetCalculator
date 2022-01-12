// reference: https://www.freecodecamp.org/news/jquery-ajax-post-method/
function printResult(content) {
    let result = ""
    result = result + "NetID:            " + content[0].NetId + "\n";
    result = result + "First:               " + content[0].First + "\n";
    result = result + "Last:               " + content[0].Last + "\n";
    result = result + "Broadcast:      " + content[0].Broadcast + "\n";
    result = result + "Subnet mask:  " + content[0].SubnetMask + "\n";
    result = result + "Available:        " + content[0].Available + "\n";
    return result;
}

function clearPromptSameSubnet() {
    document.getElementById('alertBoxSameSubnet').style.display = "none";
    document.getElementById('isSameTrue').style.display = "none";
    document.getElementById('isSameFalse').style.display = "none";
}

function clearPromptCIDR() {
    document.getElementById('alertBoxCIDR').style.display = "none";
    $("#result").val('');
}

function submitSameSubnetForm() {
    $("#sameSubnetForm").submit(function (event) {
        // Stop form from submitting normally
        event.preventDefault();

        // Get some values from elements on the page:
        var $form = $(this),
            SameSubFirstIP = $form.find("input[name='SameSubFirstIP']").val(),
            SameSubSecondIP = $form.find("input[name='SameSubSecondIP']").val(),
            SameSubSubnet = $form.find("input[name='SameSubSubnet']").val()
        url = "/sameSubnet";

        // Send the data using post
        var posting = $.post(url, {SameSubFirstIP: SameSubFirstIP, SameSubSecondIP: SameSubSecondIP, SameSubSubnet:SameSubSubnet});
        // Put the results in a div
        posting.done(function( data ) {
            var response = $(data);
            document.getElementById('alertBoxSameSubnet').style.display = "none";
            if (response[0].validityResult === true) {
                if (response[0].isSameSubNet === true) {
                    document.getElementById('isSameTrue').style.display = "block";
                    document.getElementById('isSameFalse').style.display = "none";
                } else {
                    document.getElementById('isSameTrue').style.display = "none";
                    document.getElementById('isSameFalse').style.display = "block";
                }
            } else {
                document.getElementById('alertBoxSameSubnet').style.display = "block";
                console.log(response[0].errorMessage);
                console.log(response[0].errorMessage.toString());
                $("#alertBoxSameSubnet").text("These inputs are not in IP address format: " + response[0].errorMessage.toString());
            }
        });
    });
}

function submitCIDR(){
    // Attach a submit handler to the form
    $("#CIDRForm").submit(function (event) {
        // Stop form from submitting normally
        event.preventDefault();

        // Get some values from elements on the page:
        var $form = $(this),
            inputIP = $form.find("input[name='CIDRinput']").val(),
            inputSuffix = $form.find("input[name='inputSuffix']").val(),
            url = "/calcCIDR";

        // Send the data using post
        var posting = $.post(url, {inputIP: inputIP, inputSuffix: inputSuffix});
        // Put the results in a div
        posting.done(function( data ) {
            document.getElementById('alertBoxCIDR').style.display = "none";
            var response = $(data);
            if (response[0].validityResult === true) {
                $("#result").val( printResult(response) );
            } else {
                document.getElementById('alertBoxCIDR').style.display = "block";
                $("#alertBoxCIDR").text(response[0].errorMessage);
            }
        });
    });
}

$(document).ready(function() {
    submitSameSubnetForm();
    submitCIDR();
});