// Initialize Firebase
var config = {
	apiKey: "AIzaSyC41g8OLzNIoRVQpmLi26fVSyCN2DpeY8Q",
    authDomain: "bus-loading-system.firebaseapp.com",
    databaseURL: "https://bus-loading-system.firebaseio.com",
    projectId: "bus-loading-system",
    storageBucket: "bus-loading-system.appspot.com",
    messagingSenderId: "499514943234"
};
firebase.initializeApp(config);

// points to the child in the firebase
let realTimeRef = firebase.database().ref("RTData");

// if the RTData has new entry
realTimeRef.on("child_added", snap => {
	let data = [];
	console.log("naay update!!!");
	let rfid = snap.child("rfid").val();
	let time_in = snap.child("time_in").val();
	let time_out = snap.child("time_out").val();
	let loading_bay = snap.child("loading_bay").val();
	console.log(time_out);

	// finf the bus info given the rfid
	let busInfoRef = firebase.database().ref("Bus_Info/" + rfid);
	busInfoRef.on("value", snap =>{
		data.push(snap.child("company").val()); // 0
		data.push(snap.child("plate_num").val()); // 1
		data.push(snap.child("type").val()); // 2
		data.push(snap.child("destination").val()); // 3
		let timeIn = time_in.split(" ");
		let timeInSec = hmsToSecondsOnly(timeIn[1]) + 1800;
		data.push(secondsToHms(timeInSec)); // 4
		data.push(loading_bay); // 5
		let status = (time_out === null) ? " style=\"color: #30ff00\">BOARDING" : " style=\"color: #ff0000\">DEPARTED";
		data.push(status); // 6
		addRow(data, rfid); 
	});
});

// if time_out and duration have already been set
realTimeRef.on('child_changed', snap => {
	let rfid = snap.child("rfid").val();
	let time_out = snap.child("time_out").val();
	document.getElementById(rfid).innerHTML = "<span style='color: #ff0000;'>DEPARTED</span>";

});

// adds the data in the table
function addRow(data, rfid) {
	const dataInput = '<tr>'
	+'<td>' + data[0] + '</td>'
	+'<td>' + data[1] + ' (' + data[2] + ')' + '</td>'
	+'<td>' + data[3] + '</td>'
	+'<td>' + data[4] + '</td>'
	+'<td>' + data[5] + '</td>'
	+'<td id='+rfid + data[6] + '</td>'
	+'</tr>';
	$("#dataTable").prepend(dataInput);
}
// converts HH:MM:SS to seconds
function hmsToSecondsOnly(str) {
var p = str.split(':'),
    s = 0, m = 1;
	while (p.length > 0) {
	    s += m * parseInt(p.pop(), 10);
	    m *= 60;
	}
	return s;
}

// converts seconds to HH:MM:SS
function secondsToHms(d) {
	d = Number(d);
	d = d > 86400 ? d - 86400 : d;
    var h = Math.floor(d / 3600);
    var m = Math.floor(d % 3600 / 60);
    var s = Math.floor(d % 3600 % 60);
    var time = ('0' + h).slice(-2) + ":" + ('0' + m).slice(-2)  + ":" + ('0' + s).slice(-2);
    return tConvert(time);
}

// converts the time to 12-HOUR format
function tConvert (time) {
  // Check correct time format and split into components
  time = time.toString ().match (/^([01]\d|2[0-3])(:)([0-5]\d)(:[0-5]\d)?$/) || [time];
      console.log("TIME:  " + time);

  if (time.length > 1) { // If time format correct
    time = time.slice (1);  // Remove full string match value
    time[5] =+ time[0] < 12 ? ' AM' : ' PM'; // Set AM/PM
    time[0] =+ time[0] % 12 || 12; // Adjust hours
  }
  return time.join(''); // return adjusted time or original string
}

// displays the clock in the monitor
function init() {
  timeDisplay = document.createTextNode ("");
  document.getElementById("clock").appendChild (timeDisplay);
}

function updateClock() {
  var currentTime = new Date ( );
  var currentHours = currentTime.getHours ( );
  var currentMinutes = currentTime.getMinutes ( );
  var currentSeconds = currentTime.getSeconds ( );

  // Pad the minutes and seconds with leading zeros, if required
  currentMinutes = ( currentMinutes < 10 ? "0" : "" ) + currentMinutes;
  currentSeconds = ( currentSeconds < 10 ? "0" : "" ) + currentSeconds;
  // Choose either "AM" or "PM" as appropriate
  var timeOfDay = ( currentHours < 12 ) ? "AM" : "PM";
  // Convert the hours component to 12-hour format if needed
  currentHours = ( currentHours > 12 ) ? currentHours - 12 : currentHours;
  // Convert an hours component of "0" to "12"
  currentHours = ( currentHours == 0 ) ? 12 : currentHours;
  // Compose the string for display
  var currentTimeString = currentHours + ":" + currentMinutes + ":" + currentSeconds + " " + timeOfDay;
  // Update the time display
  document.getElementById("clock").firstChild.nodeValue = currentTimeString;
}

// loads the announcement when opening the monitor
let announcementRef = firebase.database().ref("Announcement");
	announcementRef.on("child_added", snap => {
	let text = snap.child("announcement").val();
	document.getElementById("announcement").innerHTML =
	 "<marquee>"+text+"</marquee>";
});
	// sets the announcement when changed
	announcementRef.on('child_changed', snap => {
	let text = snap.child("announcement").val();
	document.getElementById("announcement").innerHTML =
	 "<marquee>"+text+"</marquee>";
});











