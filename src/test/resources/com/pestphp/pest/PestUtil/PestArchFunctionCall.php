<?php

arch()->preset()->laravel();
arch()->preset()->laravel()->ignoring("A");
arch()->preset()->laravel()->ignoring(["A"]);
arch()->preset()->laravel()->ignoring(array("A"));
arch()->expect('src')->toUseStrictTypes()->not->toUse(['die', 'dd', 'dump']);