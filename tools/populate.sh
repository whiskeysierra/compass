#!/bin/sh -e

alias jq='jq -n'
alias http='http --check-status'

jq '{schema:{type:"string"},relation:"=",description:".."}' | http PUT :8080/dimensions/country
jq '{schema:{type:"string",format:"date-time"},relation:"<",description:".."}' | http PUT :8080/dimensions/before
jq '{schema:{type:"string",format:"date-time"},relation:">",description:".."}' | http PUT :8080/dimensions/after

jq '{schema:{type:"number"},description:".."}' | http PUT :8080/keys/tax-rate

jq '{value:0.19}' | http PUT :8080/keys/tax-rate/value country==AT
jq '{value:0.08}' | http PUT :8080/keys/tax-rate/value country==CH
jq '{value:0.19}' | http PUT :8080/keys/tax-rate/value country==DE
jq '{value:0.2}' | http PUT :8080/keys/tax-rate/value country==FR

jq '{values:[
  {dimensions:{country:"DE"},value:0.19},
  {dimensions:{country:"AT"},value:0.2},
  {dimensions:{country:"CH"},value:0.08},
  {dimensions:{country:"FR"},value:0.2}
]}' | http PUT :8080/keys/tax-rate/values

jq '{schema:{type:"integer",format:"int64"},description:".."}' | http PUT :8080/keys/population

jq '{values:[
    {dimensions:{country:"AD"},value:84000},
    {dimensions:{country:"AE"},value:4975593},
    {dimensions:{country:"AF"},value:29121286},
    {dimensions:{country:"AG"},value:86754},
    {dimensions:{country:"AI"},value:13254},
    {dimensions:{country:"AL"},value:2986952},
    {dimensions:{country:"AM"},value:2968000},
    {dimensions:{country:"AO"},value:13068161},
    {dimensions:{country:"AQ"},value:0},
    {dimensions:{country:"AR"},value:41343201},
    {dimensions:{country:"AS"},value:57881},
    {dimensions:{country:"AT"},value:8205000},
    {dimensions:{country:"AU"},value:21515754},
    {dimensions:{country:"AW"},value:71566},
    {dimensions:{country:"AX"},value:26711},
    {dimensions:{country:"AZ"},value:8303512},
    {dimensions:{country:"BA"},value:4590000},
    {dimensions:{country:"BB"},value:285653},
    {dimensions:{country:"BD"},value:156118464},
    {dimensions:{country:"BE"},value:10403000},
    {dimensions:{country:"BF"},value:16241811},
    {dimensions:{country:"BG"},value:7148785},
    {dimensions:{country:"BH"},value:738004},
    {dimensions:{country:"BI"},value:9863117},
    {dimensions:{country:"BJ"},value:9056010},
    {dimensions:{country:"BL"},value:8450},
    {dimensions:{country:"BM"},value:65365},
    {dimensions:{country:"BN"},value:395027},
    {dimensions:{country:"BO"},value:9947418},
    {dimensions:{country:"BQ"},value:328},
    {dimensions:{country:"BR"},value:201103330},
    {dimensions:{country:"BS"},value:301790},
    {dimensions:{country:"BT"},value:699847},
    {dimensions:{country:"BV"},value:0},
    {dimensions:{country:"BW"},value:2029307},
    {dimensions:{country:"BY"},value:9685000},
    {dimensions:{country:"BZ"},value:314522},
    {dimensions:{country:"CA"},value:33679000},
    {dimensions:{country:"CC"},value:628},
    {dimensions:{country:"CD"},value:70916439},
    {dimensions:{country:"CF"},value:4844927},
    {dimensions:{country:"CG"},value:3039126},
    {dimensions:{country:"CH"},value:7581000},
    {dimensions:{country:"CI"},value:21058798},
    {dimensions:{country:"CK"},value:21388},
    {dimensions:{country:"CL"},value:16746491},
    {dimensions:{country:"CM"},value:19294149},
    {dimensions:{country:"CN"},value:1330044000},
    {dimensions:{country:"CO"},value:47790000},
    {dimensions:{country:"CR"},value:4516220},
    {dimensions:{country:"CU"},value:11423000},
    {dimensions:{country:"CV"},value:508659},
    {dimensions:{country:"CW"},value:141766},
    {dimensions:{country:"CX"},value:1500},
    {dimensions:{country:"CY"},value:1102677},
    {dimensions:{country:"CZ"},value:10476000},
    {dimensions:{country:"DE"},value:81802257},
    {dimensions:{country:"DJ"},value:740528},
    {dimensions:{country:"DK"},value:5484000},
    {dimensions:{country:"DM"},value:72813},
    {dimensions:{country:"DO"},value:9823821},
    {dimensions:{country:"DZ"},value:34586184},
    {dimensions:{country:"EC"},value:14790608},
    {dimensions:{country:"EE"},value:1291170},
    {dimensions:{country:"EG"},value:80471869},
    {dimensions:{country:"EH"},value:273008},
    {dimensions:{country:"ER"},value:5792984},
    {dimensions:{country:"ES"},value:46505963},
    {dimensions:{country:"ET"},value:88013491},
    {dimensions:{country:"FI"},value:5244000},
    {dimensions:{country:"FJ"},value:875983},
    {dimensions:{country:"FK"},value:2638},
    {dimensions:{country:"FM"},value:107708},
    {dimensions:{country:"FO"},value:48228},
    {dimensions:{country:"FR"},value:64768389},
    {dimensions:{country:"GA"},value:1545255},
    {dimensions:{country:"GB"},value:62348447},
    {dimensions:{country:"GD"},value:107818},
    {dimensions:{country:"GE"},value:4630000},
    {dimensions:{country:"GF"},value:195506},
    {dimensions:{country:"GG"},value:65228},
    {dimensions:{country:"GH"},value:24339838},
    {dimensions:{country:"GI"},value:27884},
    {dimensions:{country:"GL"},value:56375},
    {dimensions:{country:"GM"},value:1593256},
    {dimensions:{country:"GN"},value:10324025},
    {dimensions:{country:"GP"},value:443000},
    {dimensions:{country:"GQ"},value:1014999},
    {dimensions:{country:"GR"},value:11000000},
    {dimensions:{country:"GS"},value:30},
    {dimensions:{country:"GT"},value:13550440},
    {dimensions:{country:"GU"},value:159358},
    {dimensions:{country:"GW"},value:1565126},
    {dimensions:{country:"GY"},value:748486},
    {dimensions:{country:"HK"},value:6898686},
    {dimensions:{country:"HM"},value:0},
    {dimensions:{country:"HN"},value:7989415},
    {dimensions:{country:"HR"},value:4284889},
    {dimensions:{country:"HT"},value:9648924},
    {dimensions:{country:"HU"},value:9982000},
    {dimensions:{country:"ID"},value:242968342},
    {dimensions:{country:"IE"},value:4622917},
    {dimensions:{country:"IL"},value:7353985},
    {dimensions:{country:"IM"},value:75049},
    {dimensions:{country:"IN"},value:1173108018},
    {dimensions:{country:"IO"},value:4000},
    {dimensions:{country:"IQ"},value:29671605},
    {dimensions:{country:"IR"},value:76923300},
    {dimensions:{country:"IS"},value:308910},
    {dimensions:{country:"IT"},value:60340328},
    {dimensions:{country:"JE"},value:90812},
    {dimensions:{country:"JM"},value:2847232},
    {dimensions:{country:"JO"},value:6407085},
    {dimensions:{country:"JP"},value:127288000},
    {dimensions:{country:"KE"},value:40046566},
    {dimensions:{country:"KG"},value:5776500},
    {dimensions:{country:"KH"},value:14453680},
    {dimensions:{country:"KI"},value:92533},
    {dimensions:{country:"KM"},value:773407},
    {dimensions:{country:"KN"},value:51134},
    {dimensions:{country:"KP"},value:22912177},
    {dimensions:{country:"KR"},value:48422644},
    {dimensions:{country:"XK"},value:1800000},
    {dimensions:{country:"KW"},value:2789132},
    {dimensions:{country:"KY"},value:44270},
    {dimensions:{country:"KZ"},value:15340000},
    {dimensions:{country:"LA"},value:6368162},
    {dimensions:{country:"LB"},value:4125247},
    {dimensions:{country:"LC"},value:160922},
    {dimensions:{country:"LI"},value:35000},
    {dimensions:{country:"LK"},value:21513990},
    {dimensions:{country:"LR"},value:3685076},
    {dimensions:{country:"LS"},value:1919552},
    {dimensions:{country:"LT"},value:2944459},
    {dimensions:{country:"LU"},value:497538},
    {dimensions:{country:"LV"},value:2217969},
    {dimensions:{country:"LY"},value:6461454},
    {dimensions:{country:"MA"},value:33848242},
    {dimensions:{country:"MC"},value:32965},
    {dimensions:{country:"MD"},value:4324000},
    {dimensions:{country:"ME"},value:666730},
    {dimensions:{country:"MF"},value:35925},
    {dimensions:{country:"MG"},value:21281844},
    {dimensions:{country:"MH"},value:65859},
    {dimensions:{country:"MK"},value:2062294},
    {dimensions:{country:"ML"},value:13796354},
    {dimensions:{country:"MM"},value:53414374},
    {dimensions:{country:"MN"},value:3086918},
    {dimensions:{country:"MO"},value:449198},
    {dimensions:{country:"MP"},value:53883},
    {dimensions:{country:"MQ"},value:432900},
    {dimensions:{country:"MR"},value:3205060},
    {dimensions:{country:"MS"},value:9341},
    {dimensions:{country:"MT"},value:403000},
    {dimensions:{country:"MU"},value:1294104},
    {dimensions:{country:"MV"},value:395650},
    {dimensions:{country:"MW"},value:15447500},
    {dimensions:{country:"MX"},value:112468855},
    {dimensions:{country:"MY"},value:28274729},
    {dimensions:{country:"MZ"},value:22061451},
    {dimensions:{country:"NA"},value:2128471},
    {dimensions:{country:"NC"},value:216494},
    {dimensions:{country:"NE"},value:15878271},
    {dimensions:{country:"NF"},value:1828},
    {dimensions:{country:"NG"},value:154000000},
    {dimensions:{country:"NI"},value:5995928},
    {dimensions:{country:"NL"},value:16645000},
    {dimensions:{country:"NO"},value:5009150},
    {dimensions:{country:"NP"},value:28951852},
    {dimensions:{country:"NR"},value:10065},
    {dimensions:{country:"NU"},value:2166},
    {dimensions:{country:"NZ"},value:4252277},
    {dimensions:{country:"OM"},value:2967717},
    {dimensions:{country:"PA"},value:3410676},
    {dimensions:{country:"PE"},value:29907003},
    {dimensions:{country:"PF"},value:270485},
    {dimensions:{country:"PG"},value:6064515},
    {dimensions:{country:"PH"},value:99900177},
    {dimensions:{country:"PK"},value:184404791},
    {dimensions:{country:"PL"},value:38500000},
    {dimensions:{country:"PM"},value:7012},
    {dimensions:{country:"PN"},value:46},
    {dimensions:{country:"PR"},value:3916632},
    {dimensions:{country:"PS"},value:3800000},
    {dimensions:{country:"PT"},value:10676000},
    {dimensions:{country:"PW"},value:19907},
    {dimensions:{country:"PY"},value:6375830},
    {dimensions:{country:"QA"},value:840926},
    {dimensions:{country:"RE"},value:776948},
    {dimensions:{country:"RO"},value:21959278},
    {dimensions:{country:"RS"},value:7344847},
    {dimensions:{country:"RU"},value:140702000},
    {dimensions:{country:"RW"},value:11055976},
    {dimensions:{country:"SA"},value:25731776},
    {dimensions:{country:"SB"},value:559198},
    {dimensions:{country:"SC"},value:88340},
    {dimensions:{country:"SD"},value:35000000},
    {dimensions:{country:"SS"},value:8260490},
    {dimensions:{country:"SE"},value:9828655},
    {dimensions:{country:"SG"},value:4701069},
    {dimensions:{country:"SH"},value:7460},
    {dimensions:{country:"SI"},value:2007000},
    {dimensions:{country:"SJ"},value:2550},
    {dimensions:{country:"SK"},value:5455000},
    {dimensions:{country:"SL"},value:5245695},
    {dimensions:{country:"SM"},value:31477},
    {dimensions:{country:"SN"},value:12323252},
    {dimensions:{country:"SO"},value:10112453},
    {dimensions:{country:"SR"},value:492829},
    {dimensions:{country:"ST"},value:175808},
    {dimensions:{country:"SV"},value:6052064},
    {dimensions:{country:"SX"},value:37429},
    {dimensions:{country:"SY"},value:22198110},
    {dimensions:{country:"SZ"},value:1354051},
    {dimensions:{country:"TC"},value:20556},
    {dimensions:{country:"TD"},value:10543464},
    {dimensions:{country:"TF"},value:140},
    {dimensions:{country:"TG"},value:6587239},
    {dimensions:{country:"TH"},value:67089500},
    {dimensions:{country:"TJ"},value:7487489},
    {dimensions:{country:"TK"},value:1466},
    {dimensions:{country:"TL"},value:1154625},
    {dimensions:{country:"TM"},value:4940916},
    {dimensions:{country:"TN"},value:10589025},
    {dimensions:{country:"TO"},value:122580},
    {dimensions:{country:"TR"},value:77804122},
    {dimensions:{country:"TT"},value:1228691},
    {dimensions:{country:"TV"},value:10472},
    {dimensions:{country:"TW"},value:22894384},
    {dimensions:{country:"TZ"},value:41892895},
    {dimensions:{country:"UA"},value:45415596},
    {dimensions:{country:"UG"},value:33398682},
    {dimensions:{country:"UM"},value:0},
    {dimensions:{country:"US"},value:310232863},
    {dimensions:{country:"UY"},value:3477000},
    {dimensions:{country:"UZ"},value:27865738},
    {dimensions:{country:"VA"},value:921},
    {dimensions:{country:"VC"},value:104217},
    {dimensions:{country:"VE"},value:27223228},
    {dimensions:{country:"VG"},value:21730},
    {dimensions:{country:"VI"},value:108708},
    {dimensions:{country:"VN"},value:89571130},
    {dimensions:{country:"VU"},value:221552},
    {dimensions:{country:"WF"},value:16025},
    {dimensions:{country:"WS"},value:192001},
    {dimensions:{country:"YE"},value:23495361},
    {dimensions:{country:"YT"},value:159042},
    {dimensions:{country:"ZA"},value:49000000},
    {dimensions:{country:"ZM"},value:13460305},
    {dimensions:{country:"ZW"},value:13061000},
    {dimensions:{country:"CS"},value:10829175},
    {dimensions:{country:"AN"},value:300000}
]}' | http PUT :8080/keys/population/values
