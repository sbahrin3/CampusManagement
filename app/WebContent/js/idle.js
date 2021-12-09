//this minutes var is not actually minutes, it depends on window.setInterval

var minutes = 15;
var timeOut = minutes;
var secondsCounter = 0;

console.log("time out = " + timeOut);

document.onclick = function() {
    secondsCounter = 0;
};
document.onmousemove = function() {
    secondsCounter = 0;
};
document.onmouseover = function() {
    secondsCounter = 0;
};
document.onkeypress = function() {
    secondsCounter = 0;
};

window.setInterval(checkIdleTime, 60000);

//window.setInterval(checkIdleTime, 1000);

function checkIdleTime() {
    secondsCounter++;
    if (secondsCounter >= timeOut) {
        //document.location.href = "../logout3.jsp";
        secondsCounter = 0;
    }
    console.log(secondsCounter);
}