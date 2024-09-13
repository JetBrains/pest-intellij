<?php

class SomeTrait {
    public function traitFunc(){}
}

pest()->extend(SomeTrait::class);

it('has home', function (){
    $this-><caret>
});