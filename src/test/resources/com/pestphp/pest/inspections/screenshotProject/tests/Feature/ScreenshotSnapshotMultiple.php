<?php

it('test2', function () {
    $page = visit('/');
    $page->assertScreenshotMatches();
});

it('test', function () {
    $page = visit('/');
    $page->assertScreenshotMatches();
    
    $page2 = visit('/home');
    $page2->assertScreenshotMatches();
});
