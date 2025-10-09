<?php

it('1_2-3^4!', function () {
    $page = visit('/');
    $page-><warning descr="Missing screenshot snapshot, run the test to create it">assertScreenshotMatches</warning>();
});
