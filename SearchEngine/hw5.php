<?php 
    ini_set ('memory_limit', '-1');
    ini_set ('max_execution_time', 50000);
	include 'spellcorrector/SpellCorrector.php';
	
	// make sure browsers see this page as utf-8 encoded HTML 
	header('Content-Type: text/html; charset=utf-8'); 

	$limit = 10; 
	$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false; 
	$results = false; 

	$path = "/Users/WeiLi/Downloads/solr-7.1.0/WSJ/WSJ/";
	//load csv file
	$file = fopen("WSJ map.csv", 'r');
	$csv = array();

	while(($line = fgetcsv($file)) !== FALSE){
	//	echo $line[0]."=".$line[1]."\n";
		$csv[$line[0]] = $line[1];
	}
	fclose($file);

	$additionalParameters = array(
 		'sort' => 'pageRankFile desc'
	);

	if ($query) 
	{ 
		// The Apache Solr Client library should be on the include path 
		// which is usually most easily accomplished by placing in the 
		// same directory as this script ( . or current directory is a default 
		// php include path entry in the php.ini) 
		require_once('/Users/WeiLi/work/Apache2/572_homework4/solr-php-client-master/Apache/Solr/Service.php'); 

		// create a new solr service instance - host, port, and corename 
		// path (all defaults in this example) 
		$solr = new Apache_Solr_Service('localhost', 8983, "/solr/hw4/"); 

		// if magic quotes is enabled then stripslashes will be needed 
		if (get_magic_quotes_gpc() == 1) 
		{ 
			$query = stripslashes($query); 
		} 

		// in production code you'll always want to use a try /catch for any 
		// possible exceptions emitted by searching (i.e. connection 
		// problems or a query parsing error) 
		try 
		{ 
			if(isset($_GET["choice"]) && $_GET["choice"] == "default"){
				$results = $solr->search($query, 0, $limit); 
			}
			else{
				$results = $solr->search($query, 0, $limit, $additionalParameters); 
			}
		}
		catch (Exception $e) 
		{ 
		// in production you'd probably log or email this error to an admin 
		// and then show a special message to the user but for this example 
		// we're going to show the full exception 
			die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>"); 
		} 
	} 

    function getSnippet($query,$id){
        $id = substr($id, 0, strpos($id, ".html"));
	    $content = file_get_contents("plaintext/".$id.".txt",true);
	    $content = preg_replace("!\s+!"," ",$content);
        $subtitute = strtolower($content);
	    $snippet = "";
        $len = count($query);
      //  $pos = strpos($subtitute, strtoLower($query[0])." ".strtoLower($query[1]));
        $pos = strpos($subtitute, strtoLower(implode(" ", $query)));
        if($pos === false){          
                $pos = strpos($subtitute, strtoLower($query[0])." ");
                if($pos === false){
                    $pos = strpos($subtitute, strtoLower($query[1])." ");{
                        if($pos == false){
                            $snippet = "";
                        }else{
                            $snippet = substr($content,$pos,200)."...";
                        }
                    }
                }else{
                    $snippet = substr($content,$pos,200)."...";
                }            
        }else{
            $snippet = substr($content,$pos,200)."...";
        }
        return $snippet;
    }

    function showSnippet($query,$snippet){ 
	   foreach($query as $term){
		  $snippet = preg_replace('/'.$term.'/i',"<span class='snippet'>\$0</span>",$snippet);
	   }
	   return $snippet;
    }
?> 
<html> 
	<head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
        
        <script type="text/javascript">
            $(function(){
                var url_head =  "http://localhost:8983/solr/hw4/suggest?q=";
                var url_tail = "&wt=json";
                $("#q").autocomplete({
                    source : function(request, response){
                        var query = $("#q").val().trim();
                        var curWord = $("#q").val().toLowerCase().split(" ").pop(-1);
                        var url = url_head + curWord + url_tail;
                        $.ajax({
                            url : url,
                            success : function(data){
                                var curWord = $("#q").val().toLowerCase().split(" ").pop(-1);
                                var suggestions = data.suggest.suggest[curWord].suggestions;
                         //       alert(suggestions[5].term);
                                var suggestionList = [];
                                
                                var len = Math.min(5,suggestions.length);
                                for(var i = 0; i < len; i++){
                               /*     if(i == 0 && suggestions[i].term != curWord){
                                        suggestionList.push(query);
                                    }   */
                                    suggestionList.push(query.substr(0,query.lastIndexOf(" ")+1)+suggestions[i].term);    
                                }
                                response(suggestionList);
                            },
                            dataType: 'jsonp',
                            jsonp: 'json.wrf'
                        });
                    },
                    
                });
            });
            
		</script>
        
        <style>
            .snippet{
                font-weight: bold;
            }
        </style>
		<title>PHP Solr Client of WSJ</title> 
	</head> 
	<body> 
		<div style="position:absolute; top:0px; left:0px; width:1280px; height:90px; background-color:#f4f4f4; border:solid #d6d8d8 thin;">
			<form accept-charset="utf-8" method="get"> 
                <img style="position: absolute; top:25px; left:20px; width:120px; height:40px; "src="usc.png" >
				
				<label style="position:absolute; top:40px; left: 150px; font-size:25px;" for="q" >Search</label> 
				<input style="position:absolute; top:38px; left: 220px; width:600px; height:30px; font-size:18px;" id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
				<input style="position:absolute; top:45px; left: 820px;" type="radio" name="choice" value="default" checked <?php if(isset($_GET["choice"]) && $_GET["choice"] == "default") echo "checked" ?>>
                <span style="position:absolute; top:48px; left: 840px; font-size:15px;">Solr Lucene</span> 
				<input style="position:absolute; top:45px; left: 915px;" type="radio" name="choice" value="pageRank" <?php if(isset($_GET["choice"]) && $_GET["choice"] == "pageRank") echo "checked" ?>>
                <span style="position:absolute; top:48px; left: 935px; font-size:15px;">PageRank</span> 
                <br />
				<br />
				<input style="position:absolute; top:42px; left: 1010px; width:100px; height:25px; font-size:15px;" type="submit" value="submit"/>
			</form>
		</div>
        
		<div style="position:absolute; top:100px; left:30px; width:1200px;">
        <?php
                if($query){
                    $query = strtolower($query);
                    $arr = explode(" ", $query);
                    $flag = false;
                    $correction = array();
                    for($i = 0; $i < sizeof($arr); $i++){
                        $word = $arr[$i];
                        $correctWord = SpellCorrector::correct($word);
                        if($correctWord != $word){
                            $flag = true;
                        }
                        array_push($correction,$correctWord);
                    }
                    if($flag){
                        $corrects = implode("+", $correction);
                        $display = implode(" ", $correction);
        ?>
            <div style="position:absolute; top:6px; left:30px; font-size:20px;">
                  Showing results for <i> <b> <a href="http://localhost/572_homework4/hw5.php?choice=<?= $_GET["choice"]?>&q=<?=$corrects ?>">  <?=$display; ?></a> </b> </i>
            </div>
        <?php
                    }
                }                
        ?>

<?php 
// display results 
	if ($results) 
	{ 
		$total = (int) $results->response->numFound; 
		$start = min(1, $total); 
		$end = min($limit, $total); 
?> 

			<div style="position:absolute; top:30px; left:30px;">  Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div> 
			<div style="position:absolute; top:60px; left:30px;"> 

<?php 
	// iterate result documents 
	foreach ($results->response->docs as $doc) 
	{         
		$id = "";
		$title = "";
		$desc = "";
		$url = "";
        // iterate document fields / values 
		foreach ($doc as $field => $value) 
		{ 
			if($field == "title"){
				$title = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
			}
			if($field == "description"){
				$desc = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
			}
			if($field == "id"){
				$id = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
				$id = str_replace($path, "", $id);
				$url = $csv[$id];
			} 
		} 
        $query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
        $query = explode(" ",$query);
        $pos = strpos($desc, $query[0]);
            
            if($pos === false){
                $snippet = getSnippet($query, $id);
            }else{
                $snippet = $desc;
            }
        
		echo "<a style='color:blue; font-size:18px;' href='{$url}' target='_blank'> ".$title."</a> <br />";
		echo "<a style='color:green;' href='{$url}' target='_blank'> ".$url."</a> <br />";
	//	echo "<b>Id: </b>".$id."<br />";
	//	echo $desc."<br /><br /><br />";
    //  echo $query."   ".$pos;
        echo showSnippet($query, $snippet)."<br /><br /><br />";
	 }	 
?> 
			</div> 
<?php 
	}           
?> 
		</div>
	</body> 
</html>