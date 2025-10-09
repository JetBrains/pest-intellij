<?php

it('test2', function () {
    $page = visit('/');

    $page-><warning descr="Missing screenshot snapshot, run the test to create it">assertScreenshotMatches</warning>();
});

it('test', function () {
    $page = visit('/');

    $page-><warning descr="Missing screenshot snapshot, run the test to create it">assertScreenshotMatches</warning>();

    $page2 = visit('/home');

    $page2-><warning descr="Missing screenshot snapshot, run the test to create it">assertScreenshotMatches</warning>();
});

