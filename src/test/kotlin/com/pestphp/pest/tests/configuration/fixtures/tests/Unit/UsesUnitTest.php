<?php

class SomeTrait {
    public function traitFunc(){}
}

uses(SomeTrait::class);

it('has home', function (){
    $this-><caret>
});