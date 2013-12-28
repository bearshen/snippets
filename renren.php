<?php
class Hybrid_Providers_Renren extends Hybrid_Provider_Model_OAuth2
{
    public $scope = '';
    function initialize() 
    {   
        parent::initialize();
        if ( ! $this->config["keys"]["id"] || ! $this->config["keys"]["secret"] ){
            throw new Exception( "Your application id and secret are required in order to connect to {$this->providerId}.", 4 );
        }
 
        $this->api->api_base_url = "http://api.renren.com/restserver.do";
        $this->api->authorize_url  = "https://graph.renren.com/oauth/authorize";
        $this->api->token_url      = "https://graph.renren.com/oauth/token";
    }
 
    function getUserProfile()
    {
        $parameters = array('method' => 'users.getInfo', 'fields'=>'uid,name,sex,birthday,headurl,hometown_location', 'v'=>'1.0', 'access_token' => $this->api->access_token);
        $parameters['format'] = 'json';
        $parameters['sig'] = $this->generateSignature($parameters);
 
        $response = $this->api->post('',$parameters);
 
        if (!is_array($response)) {
            $profile = json_decode($response);
        }
        else {
            $profile = $response[0];
        }
 
        if (! $profile->uid) {
            throw new Exception ( "HybridAuth did not successfully authenticate the user.", 6);
        }
 
        $this->user->profile->identifier    = $profile->uid;
        $this->user->profile->displayName      = $profile->name;
        $this->user->profile->profileURL   = 'http://www.renren.com/'.$profile->uid.'/profile';
        $this->user->profile->photoURL         = $profile->headurl;
        $pieces = explode ("-", $profile->birthday);
        $this->user->profile->birthDay      = $pieces [2];
        $this->user->profile->birthMonth    = $pieces [1];
        $this->user->profile->birthYear     = $pieces [0];
        switch ( $profile->sex ) {
            case '1': $this->user->profile->gender = 'male'; break;
            case '2': $this->user->profile->gender = 'female'; break;
        }
        return $this->user->profile;
    }
 
    private function generateSignature($arr){
        ksort($arr);
        reset($arr);
        $str = '';
        foreach($arr AS $k=>$v){
            $v = utf8_encode ($v);
            $arr[$k]=$v;
            $str .= $k.'='.$v;
        }
        $str .= $this->config['keys']['secret'];
        $str = md5($str);
        return $str;
    }
}