<?php

expect()->extend('toThis', function () {
    return $this->toBeEmpty();
});