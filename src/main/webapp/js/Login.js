// (function () {
//     document.getElementById("LoginButton").addEventListener('click', (e => {
//         var form = e.target.closest("form");
//         if (form.checkValidity()) {
//             makeCall("POST", "CheckLogin", e.target.closest('form'),
//                 function (request) {
//                     if (request.readyState === XMLHttpRequest.DONE) {
//                         var message = request.responseText;
//                         switch (request.status) {
//                             case 200:   //Everything is ok
//                                 sessionStorage.setItem('username', message);
//                                 location.replace("activePage.html");
//                                 break;
//                             case 400:   //Returned bad request
//                                 document.getElementById("errorMessage").innerHTML = message;
//                                 document.getElementById("errorMessage").style.visibility = "visible";
//                                 break;
//                             case 401:   //Not authorized
//                                 document.getElementById("errorMessage").innerHTML = message;
//                                 document.getElementById("errorMessage").style.visibility = "visible";
//                                 break;
//                             case 500:
//                                 document.getElementById("errorMessage").innerHTML = message;
//                                 document.getElementById("errorMessage").style.visibility = "visible";
//                                 break;
//                             default :
//                                 document.getElementById("errorMessage").innerHTML = "generic error";
//                                 document.getElementById("errorMessage").style.visibility = "visible";
//                                 break;
//                         }
//                     }
//                 })
//
//         } else form.reportValidity();
//     }))
// })();

/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

    document.getElementById("LoginButton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", 'checkLogin', e.target.closest("form"),
                function(x) {
                    if (x.readyState == XMLHttpRequest.DONE) {
                        var message = x.responseText;
                        switch (x.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "activePage";
                                break;
                            case 400: // bad request
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errorMessage").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

})();