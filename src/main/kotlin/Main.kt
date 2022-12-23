// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.model.UploadStorageType
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.jetbrains.skia.impl.Log
import org.json.JSONObject
import java.io.IOException
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.concurrent.timerTask
import kotlin.math.min


var text = mutableStateOf("Nothing yet")
var ISPName = mutableStateOf("-")
var dataUsed = mutableStateOf(0)
var homeScreenElementsShown = mutableStateOf(true)

var downloadSpeed = mutableStateOf(0f)
var uploadSpeed = mutableStateOf(0.000000000000000000000000000000000000000000000000000008f)

var currentTest = mutableStateOf("download")

var percentageBar = mutableStateOf(0f)
var animationDuration = mutableStateOf(250)

var speedTestForeGroundColor = mutableStateOf(Color(0XFFc6c5ff))

var displayArc = mutableStateOf(true)

var displayRestartButton = mutableStateOf(false)

var speedTestPannelPaddingTop = mutableStateOf(0.dp)

var averageDownloadSpeed = mutableStateOf(mutableListOf<Float>())

var averageUploadSpeed = mutableStateOf(mutableListOf<Float>())

var stopSpeedTest = mutableStateOf(false)

var speedTestCurrentlyActive = mutableStateOf(false)

@Composable
@Preview
fun pre(){
    App()
}

@Composable
@Preview
fun ArcProgressbar(
    thickness: Dp = 50.dp,
    foregroundIndicatorColor: Color = Color(0xFF35898f),
    backgroundIndicatorColor: Color = Color(0XFF141414),
    startAngle: Float = 150f,
    dataPlanLimit: Float = 100f) {
    // It remembers the number value

    //change the colour of the bar depending on if it's a download or upload test
    val gapBetweenEnds = (startAngle - 90) * 2

    val animateNumber =  animateFloatAsState(
        targetValue = percentageBar.value.toFloat(),
        animationSpec = tween(
            durationMillis = animationDuration.value
        )
    )

    // Number Animation

    var sweepAngle = (animateNumber.value / dataPlanLimit) * (360f - gapBetweenEnds).toFloat()



    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top=60.dp)
            .width(280.dp)
            .height(280.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width(290.dp)
                .height(290.dp)
        ) {
            drawImage(
                useResource("bar.png") { loadImageBitmap(it) },

                //what are the other parameters available for this function and how do I use them?
                //i want to change the width

                topLeft = Offset(30f, 30f),
                //topLeft = Offset(120f, 30f),
                //how to change the size of the drawImage function ? I want to make it smaller







            )

             withTransform(
                 {          //animateNumber.value
                     rotate((-118+(2.36*animateNumber.value)).toFloat(), Offset(size.minDimension / 2, size.minDimension / 2))
                     //rotate((-118+(2.36*animateNumber.value)).toFloat(), Offset(size.minDimension / 2, size.minDimension / 2))
                },
                 {
                     drawImage(
                         useResource("needle.png") { loadImageBitmap(it) },

                         //what are the other parameters available for this function and how do I use them?
                         //i want to change the width

                         topLeft = Offset(132f, 78f),
                         //topLeft = Offset(120f, 30f),
                         //how to change the size of the drawImage function ? I want to make it smaller







                     )
                  }
              )













            // withTransform(
            //     {
            //          rotate((-118+(2.36*animateNumber.value)).toFloat(), Offset(size.minDimension / 2, size.minDimension / 2))
            //     },
            //      {
            //         drawLine(
            //             strokeWidth = 8.dp.toPx(),
            //            cap = StrokeCap.Round,
            //             color = Color.Red,
            //             start = Offset(size.minDimension / 2, size.minDimension / 2),
            //             end = Offset(size.minDimension / 2, 12.dp.toPx())
            //         )
            //      }
            //  )

            // Background Arc
            drawArc(
                color = backgroundIndicatorColor,
                startAngle = startAngle,
                sweepAngle = 360f - gapBetweenEnds,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
            )

            // convert the number to angle


            // Foreground circle
            drawArc(
                color = speedTestForeGroundColor.value,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(thickness.toPx(), cap = StrokeCap.Butt)
            )
        }

        // Display the data usage value

    }


}









@Composable
fun Indicator(type: String, typeData:String, imagePath:String){
    Row(modifier = Modifier
        .clip(shape = RoundedCornerShape(12.dp))
        .width(370.dp)
        .height(80.dp)
        .background(Color(0xFF141414)),
        verticalAlignment = Alignment.CenterVertically




    ){


        Image(painter = painterResource("$imagePath"),
            contentDescription = "image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(start = 15.dp)
                .height(45.dp)
                .width(45.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)
                ))
        Column (modifier = Modifier
            .height(40.dp)
            .padding(start=15.dp)
            .background(Color(0X000000))
        , verticalArrangement = Arrangement.SpaceBetween
        ){

            Text(type,
                fontFamily = FontFamily(Font(
                    resource = "BRSonoma-SemiBold.otf"
                )),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start=0.dp)
                    .padding(top=0.dp),
                fontSize = 14.sp
                , color = Color(0XFF717171))


            Text(typeData,
                fontFamily = FontFamily(Font(
                    resource = "BRSonoma-SemiBold.otf"
                )),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start=0.dp)
                    .padding(top=0.dp),
                fontSize = 16.sp,
                color = Color(0XFFffffff))
        }


    }

}






@Composable
fun speedPannel(speed: Float, speedType:String, imagePath:String){
    Row(modifier = Modifier
        .clip(shape = RoundedCornerShape(12.dp))
        .width(187.5.dp)
        .height(80.dp)
        .background(Color(0xFF00FFFFFF)),
        verticalAlignment = Alignment.CenterVertically




    ){


        Image(painter = painterResource(imagePath),
            contentDescription = "image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(start = 15.dp)
                .height(30.dp)
                .width(30.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)
                ))
        Column (modifier = Modifier
            .height(50.dp)
            .padding(start=15.dp)
            , verticalArrangement = Arrangement.SpaceBetween
        ){

            Text(speedType,
                fontFamily = FontFamily(Font(
                    resource = "BRSonoma-Regular.otf"
                )),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start=0.dp)
                    .padding(top=5.dp),
                fontSize = 14.5.sp
                , color = Color(0XFF6e6e6e))


            var toDisplay = "Selfie"


            if (speedType == "Upload" && uploadSpeed.value == 0.000000000000000000000000000000000000000000000000000008f){
                toDisplay = " -"
            }else{
                toDisplay = "${speed.toInt()} Mbps"
            }




            Text(toDisplay,
                fontFamily = FontFamily(Font(
                    resource = "BRSonoma-SemiBold.otf"
                )),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start=0.dp)
                    .padding(top=0.dp),
                fontSize = 23.sp,
                color = Color(0XFFffffff))
        }


    }

}








@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App() {


    var downloadSpeedTestSocket = SpeedTestSocket()
    var uploadSpeedTestSocket  = SpeedTestSocket()

    downloadSpeedTestSocket.setUploadStorageType(UploadStorageType.FILE_STORAGE);
    uploadSpeedTestSocket.setUploadStorageType(UploadStorageType.FILE_STORAGE);

    downloadSpeedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport) {
            // called when download/upload is complete
            //println("[COMPLETED] rate in octet/s : " + (report.transferRateOctet.toInt()/12500))
            //println("[COMPLETED] rate in bit/s   : " + report.transferRateBit)


            dataUsed.value = dataUsed.value + 1

            percentageBar.value = 0f

            animationDuration.value = 200

            downloadSpeed.value = 0f

            Timer().schedule(timerTask {

                downloadSpeed.value = averageDownloadSpeed.value.average().toFloat()


            }, 500)










            println("Completed Download Test")




        }

        override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
            // called when a download/upload error occur
        }

        override fun onProgress(percent: Float, report: SpeedTestReport) {
            // called to notify download/upload progress
            //println("[PROGRESS] progress : $percent%")

            var coupleDownload = (report.transferRateOctet.toFloat()*0.000008).toFloat()
            downloadSpeed.value = coupleDownload

            percentageBar.value = min(downloadSpeed.value, 100f)

            averageDownloadSpeed.value.add(coupleDownload)




            // println("[PROGRESS] rate in bit/s   : " + report.transferRateBit)
        }
    })

    uploadSpeedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport) {
            // called when download/upload is complete
            //println("[COMPLETED] rate in octet/s : " + (report.transferRateOctet.toInt()/12500))
            //println("[COMPLETED] rate in bit/s   : " + report.transferRateBit)

            dataUsed.value = dataUsed.value + 1

            animationDuration.value = 250


            percentageBar.value = 0f

            animationDuration.value = 200



            Timer().schedule(timerTask {
                downloadSpeed.value = averageDownloadSpeed.value.average().toFloat()
                uploadSpeed.value = averageUploadSpeed.value.average().toFloat()

                speedTestPannelPaddingTop.value = 50.dp

                displayArc.value = false

                displayRestartButton.value = true

                speedTestCurrentlyActive.value = false
            }, 500)












        }

        override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
            // called when a download/upload error occur
        }

        override fun onProgress(percent: Float, report: SpeedTestReport) {
            // called to notify download/upload progress
            //println("[PROGRESS] progress : $percent%")

            var coupleUpload = (report.transferRateOctet.toFloat()*0.000008).toFloat()
            uploadSpeed.value = coupleUpload

            percentageBar.value = min(uploadSpeed.value, 100f)

            averageUploadSpeed.value.add(coupleUpload)

            // println("[PROGRESS] rate in bit/s   : " + report.transferRateBit)
        }
    })

    fun startSpeedTest(){
            downloadSpeed.value = 0f
            uploadSpeed.value = 0.000000000000000000000000000000000000000000000000000008f
            averageDownloadSpeed.value.clear()
            averageUploadSpeed.value.clear()

            displayRestartButton.value = false

            displayArc.value = true

            speedTestCurrentlyActive.value = true

            speedTestPannelPaddingTop.value = 0.dp






        println("Starting download test !")
        //currentTest.value = "download"

        Timer().schedule(timerTask {
            speedTestForeGroundColor.value = Color(0XFFc6c5ff)
            downloadSpeedTestSocket.startFixedDownload("https://ipv4.appliwave.testdebit.info:8080/1G.iso",12000);
        }, 1000)


        Timer().schedule(timerTask {
            println("Starting upload test !")
            speedTestForeGroundColor.value = Color(0XFFe5ffc5)
            uploadSpeed.value = 0f
            uploadSpeedTestSocket.startFixedUpload("http://bouygues.testdebit.info/ul/", 100000*1000 , 15000)
            //uploadSpeedTestSocket.startFixedUpload("https://ipv4.appliwave.testdebit.info:8080/1G.iso", 1073741824, 12000);
        }, 15000)
    }




    val client = OkHttpClient()

    val mediaType = "application/json".toMediaTypeOrNull()

    var body = RequestBody.create(
        mediaType,
        ""
    )

    val request = Request.Builder()
        //.url("https://listen-api.listennotes.com/api/v2/podcasts/" + id_ + "?sort=recent_first")
            .url("http://ip-api.com/json")
        .method("GET", null)
        //.header("X-ListenAPI-Key", "c7a88e0f1a17445bb4f14b4212fa161f")
        //.header("Accept", "application/json")
        .build()

    var response__ = ""

    client.newCall(request).enqueue(object : Callback, okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            e.printStackTrace()
        }


        override fun onResponse(call: okhttp3.Call, response_: Response) {
            response_.use {

                var response_ = response_.body!!.string()

                val gson_ = GsonBuilder().setPrettyPrinting().create()
                var prettyJson_ = gson_.toJson(
                    JsonParser.parseString(
                        response_ // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                    )
                )

               

                var obj_ = JSONObject(prettyJson_)
                text.value = obj_.getString("as").split(" ")[1].toString()

                Log.info(obj_.toString())











                //var obj_ = JSONObject(prettyJson_)
                ISPName.value = obj_.getString("as").split(" ")[1].toString()
                //var newValue = obj_.getString("as").split(" ")[1]
                //println("Api_result"+obj_.toString())

                //ISPName = "idk"





                //https://ipv4.appliwave.testdebit.info:8080/1G.iso

                //var code = obj_.getJSONObject("meta").getString("code")
            }
        }
    })
    //Second screen
    androidx.compose.animation.AnimatedVisibility(
        visible = true,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ){
        Box(
            Modifier
                .size(420.dp, 800.dp)
                .background(Color(0xFF000000))
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                , horizontalAlignment = Alignment.CenterHorizontally
            ){
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()



                // for showing snackbar in onClick for example:

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                ){

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(top = 50.dp),
                        verticalAlignment = Alignment.CenterVertically,) {
                        Text("Speedtest",
                            fontFamily = FontFamily(Font(
                                resource = "BRSonoma-SemiBold.otf"
                            )),

                            modifier = Modifier
                                .padding(start=30.dp),
                            fontSize = 28.sp
                            , color = Color(0XFFffffff)
                        )


                            IconButton(
                                onClick = {
                                    if (speedTestCurrentlyActive.value){
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "A speedtest is currently running",
                                            )
                                        }




                                    }else{
                                        homeScreenElementsShown.value = true
                                        stopSpeedTest.value = true
                                        downloadSpeed.value = 0f
                                        uploadSpeed.value = 0f

                                    }


                                    },

                                modifier = Modifier
                                    .padding(start=190.dp)
                                    .width(40.dp)
                                    .height(40.dp))
                            {
                                //add a cross iconn
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color(0XFFffffff)
                                )
                            }
                    }





                }

                Column(modifier = Modifier

                    .padding(top=20.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .width(370.dp)
                    .height(162.dp)
                    .background(Color(0xFF141414)),
                    verticalArrangement = Arrangement.Center



                ){
                    Indicator("ISP", ISPName.value,"ISPIcon.png")
                    Divider(color = Color(0XFF1b1b1b), thickness = 2.dp)
                    Indicator("Server", "Appliwave","serverIcon.png")







                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = displayArc.value,
                    enter = scaleIn(),
                    exit = scaleOut()
                ){
                    ArcProgressbar()

                }



                //Speedtest arc
                //
                //
                //







                Row(modifier = Modifier

                    .offset(y = -20.dp)
                    .padding(top=speedTestPannelPaddingTop.value)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .width(377.dp)
                    .height(80.dp)
                    .background(Color(0xFF141414))
                    , verticalAlignment = Alignment.CenterVertically



                ){
                    speedPannel(downloadSpeed.value, "Download", "DownloadIcon.png")
                    Divider(
                        color = Color(0XFF1b1b1b), //0XFF1b1b1b)
                        thickness = 2.dp,
                        modifier = Modifier
                            .height(50.dp)  //fill the max height
                            .width(2.dp)
                    )
                    speedPannel(uploadSpeed.value, "Upload", "UploadIcon.png")











                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier
                        .height(100.dp)
                        .offset(y = -0.dp),
                    backgroundColor = Color(0xFF000000)) {

                }




                //
                //
                //
                //





















                androidx.compose.animation.AnimatedVisibility(
                    visible = displayRestartButton.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    Button(onClick = {
                        if (speedTestCurrentlyActive.value){
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "A speedtest is already running",
                                    //what message should i use to tell the user to wait for the speedtest to finish


                                )
                            }




                        }else{
                            startSpeedTest()

                        }


                    }, modifier = Modifier
                        .padding(top=10.dp)
                        .offset(y = -100.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .width(370.dp)
                        .height(60.dp), colors =
                    ButtonDefaults.buttonColors(backgroundColor =
                    Color(0xFFc6c5ff)
                    )
                    ){
                        Text("Restart Test",
                            fontFamily = FontFamily(Font(
                                resource = "BRSonoma-SemiBold.otf"
                            )),
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .padding(start=0.dp)
                                .padding(top=0.dp),
                            fontSize = 15.sp
                            , color = Color(0XFF000000)
                        )
                    }

                }









            }

        }

    }

    //Home screen
    androidx.compose.animation.AnimatedVisibility(
        visible = homeScreenElementsShown.value,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ){
        Box(
            Modifier.size(420.dp, 800.dp).background(Color(0xFF000000))
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                , horizontalAlignment = Alignment.CenterHorizontally
            ){

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                ){

                    Text("Speedtest",
                        fontFamily = FontFamily(Font(
                            resource = "BRSonoma-SemiBold.otf"
                        )),

                        modifier = Modifier
                            .padding(start=30.dp)
                            .padding(top=50.dp),
                        fontSize = 28.sp
                        , color = Color(0XFFffffff)
                    )



                }

                Column(modifier = Modifier

                    .padding(top=20.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .width(370.dp)
                    .height(162.dp)
                    .background(Color(0xFF141414)),
                    verticalArrangement = Arrangement.Center



                ){
                    Indicator("ISP", ISPName.value,"ISPIcon.png")
                    Divider(color = Color(0XFF1b1b1b), thickness = 2.dp)
                    Indicator("Server", "Appliwave","serverIcon.png")







                }
                Column(modifier = Modifier

                    .padding(top=50.dp)
                    .width(370.dp)
                    .height(80.dp)




                ){
                    Text("Data Used",
                        fontFamily = FontFamily(Font(
                            resource = "BRSonoma-Regular.otf"
                        )),
                        fontWeight = FontWeight.W100,
                        modifier = Modifier
                            .padding(start=0.dp)
                            .padding(top=0.dp),
                        fontSize = 15.sp
                        , color = Color(0XFF717171)
                    )

                    Text(dataUsed.value.toString()+" GB",
                        fontFamily = FontFamily(Font(
                            resource = "BRSonoma-Medium.otf"
                        )),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(start=0.dp)
                            .padding(top=0.dp)
                            .height(44.dp)
                            .offset(y = (-5).dp),
                        fontSize = 45.sp
                        , color = Color(0XFFFFFFFFFF)
                    )

                    Text("This month",
                        fontFamily = FontFamily(Font(
                            resource = "BRSonoma-Regular.otf"
                        )),
                        fontWeight = FontWeight.W100,
                        modifier = Modifier
                            .padding(start=0.dp)
                            .padding(top=0.dp),
                        fontSize = 13.sp
                        , color = Color(0XFF2e2e2e)
                    )









                }



                Button(onClick = {
                    homeScreenElementsShown.value = false
                    Timer().schedule(timerTask {
                        startSpeedTest()
                    }, 1000)



                }, modifier = Modifier
                    .padding(top=35.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .width(370.dp)
                    .height(60.dp), colors =
                ButtonDefaults.buttonColors(backgroundColor =
                Color(0xFFc6c5ff)
                )
                ){
                    Text("Speedtest",
                        fontFamily = FontFamily(Font(
                            resource = "BRSonoma-SemiBold.otf"
                        )),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(start=0.dp)
                            .padding(top=0.dp),
                        fontSize = 15.sp
                        , color = Color(0XFF000000)
                    )
                }





            }

        }

    }








}




fun main() = application {
    val state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))


    Window(onCloseRequest = ::exitApplication, state=state) {
        App()
    }
}
