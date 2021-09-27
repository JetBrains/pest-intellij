<?php

it('renders correctly', function () {
    $data = "My super long snapshot data";

    assertMatchesSnapshot($data);
});