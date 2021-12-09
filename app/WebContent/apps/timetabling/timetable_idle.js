var minutes = 15;
var timeOut = 60 * minutes; 
var secondsCounter = 0;
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
window.setInterval(checkIdleTime, 1000);

function checkIdleTime() {
    secondsCounter++;
    if (secondsCounter >= timeOut) {
        document.location.href = "educate.timetabling.module.SetupTimeTableModule";
    }
}