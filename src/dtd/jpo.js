/* Textual Tooltip Script- (c) Dynamic Drive (www.dynamicdrive.com) For full source code, installation instructions, 100's more DHTML scripts, and Terms Of Use, visit dynamicdrive.com */
function regenerate(){
    window.location.reload()
}
function regenerate2(){
    if (document.layers){
        appear()
        setTimeout("window.onresize=regenerate",450)
    }
}
function changetext(whichcontent){
    if (document.all||document.getElementById){
        cross_el=document.getElementById? document.getElementById("descriptions"):document.all.descriptions
        cross_el.innerHTML=whichcontent
    }
    else if (document.layers){
        document.d1.document.d2.document.write(whichcontent)
        document.d1.document.close()
    }
}
function appear(){
    document.d1.visibility='show'
}
window.onload=regenerate2
