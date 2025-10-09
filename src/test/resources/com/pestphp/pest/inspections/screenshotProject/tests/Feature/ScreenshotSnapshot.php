<?php

it('browser testing', function () {
    $page = visit('/')->on()->mobile();
    $page->assertScreenshotMatches();
});
