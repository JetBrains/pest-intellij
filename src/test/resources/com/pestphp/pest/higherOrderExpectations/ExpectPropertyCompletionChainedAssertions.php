<?php

class Example {
    public string $test = "works";
    public string $otherExample = "example";
}

$example = new Example();

expect($example)
    ->test->toBeString()
    -><caret>