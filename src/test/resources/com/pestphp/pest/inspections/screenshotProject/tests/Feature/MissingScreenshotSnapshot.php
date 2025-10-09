<?php

it('browser testing', function () {
    $page = visit('/')->on()->mobile();
    $page-><warning descr="Missing screenshot snapshot, run the test to create it">assertScreenshotMatches</warning>();
});
