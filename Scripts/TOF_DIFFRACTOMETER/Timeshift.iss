# 
# Script to produce  gsas file for groups of detector IDs
# of gppd runs in higher frame. Uses the time shift operator.  
# Defaults set to second frame transformation.  
# User can either display or write a gsas file or do both.
# $Date$

$Category=Operator, Instrument Type, TOF_NPD

$Current Boolean(true)	Is this a Current Run ?

$ run_numbers         Array([21638])         Enter run numbers like [1,2:5]
$ path                DataDirectoryString    Path
$path_archive		DataDirectoryString(/IPNShome/gppduser/archive_data/)	path_archive
$ tshift              Float(33333.33)        Time shift
$ outputname          DataDirectoryString(/IPNShome/gppduser/aaaUSER_data/)   outputname
$ instrument          InstrumentNameString   Instrument

$Display_data		Boolean(true)	Display_data?
$Create_gsas		Boolean(false)	Create_gsas?
$Prompt_det_ID          Boolean(false)  Prompt_det_ID?

for i in run_numbers

if Current == true
  file_name = path&instrument&i&".RUN"
  Echo(file_name)
  load file_name,"temp"

elseif Current == false
	file_name = path_archive&instrument&i&".RUN"
  	Echo(file_name)
  	load file_name,"temp"
endif


temp[1] = TimeShift(temp[1], 30.0, tshift,true)

  id_val =["1:30,32:44,180:223","48:75,225:254","77:91,93:110,256:287","111:139,289:305,307:317","140:159"]
  focus_val = [145,125,107,90,60]
  Display temp[1]

  if Prompt_det_ID == true
  InputBox( "Enter bank info for GPPD"&i, [ "Bank 1","Bank 2","Bank 3","Bank 4","Bank 5"], id_val, [temp[1]] )
  endif

  m_dsi = TimeFocusGID(temp[1],id_val[0],focus_val[0],1.5,true)
  m_dsa = SumAtt(m_dsi, "Effective Position",true, focus_val[0]-.000005,focus_val[0]+.000005)
  for j in [1:4]
      dsi = TimeFocusGID(temp[1],id_val[j],focus_val[j],1.5,true)
      dsa = SumAtt(dsi, "Effective Position",true, focus_val[j]-0.5,focus_val[j]+0.5)
      m_dsa =Merge(m_dsa,dsa)  
  endfor

	if Display_data == true
	Display m_dsa
	endif	

	if Create_gsas == true
     	SaveGSAS( m_dsa, m_dsa, outputname&i&".gsa",false,true)
	endif

     send m_dsa
   
endfor


