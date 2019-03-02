(*******************************************************************************)
(*                           Human interface                                   *)
(*   The interface is divided in two parts: the CLI is used to display text    *)
(*              while the game itself is played in a graphic                   *)
(*******************************************************************************)

open Game

(*                         CLI part of the interface                           *)

(** Print the current scoreboard *)
let display_scores s =
  print_endline "===============================";
  List.iter (fun (n, s) -> Printf.printf "%s: %d\n" n s) s;
  print_endline "==============================="

(** Print the welcome message *)
let display_welcome () =
  print_endline "Welcome to Vector Arena !";
  if(state.phase = Attente) then print_endline "The game is about to start !"
  else (print_endline "The game is already on ! Current scores :";
        display_scores state.scores)

(** Displays the winner of the game *)
let display_winner () =
  print_endline "Game over ! Final scores :";
  display_scores state.scores

(*                     Graphic part of the interface                           *)

open Graphics

(** Open the graphic window *)
let start_graph () =
  open_graph (Printf.sprintf " %dx%d" (w*2) (h*2));
  auto_synchronize false (* We use double buffering, because we're not monsters *)

(** Draw the game's background *)
let draw_background () =
  clear_graph ();
  set_color black;
  fill_rect 0 0 (w*2) (h*2)

(** Draw the loading screen *)
let draw_loading _ =
  set_color (rgb 0 200 200);
  draw_segments [|(w/2, 4*h/3, 3*w/2, 4*h/3);
                  (w/2, 4*h/3-50, 3*w/2, 4*h/3-50)|];
  set_text_size 50; (* It unfortunally is not implemented yet... *)
  let (sx, _) = text_size "Vector Arena" in
  moveto (w - sx/2) (4*h/3-30);
  draw_string "Vector Arena"

(** Draw the shape in it's real place, but also on the corresponding borders *)
let fill_poly_tor shape =
  fill_poly shape;
  fill_poly (Array.map (fun (x, y) -> (x-2*w,y)) shape);
  fill_poly (Array.map (fun (x, y) -> (x,y-2*h)) shape);
  fill_poly (Array.map (fun (x, y) -> (x-2*w,y-2*h)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+2*w,y)) shape);
  fill_poly (Array.map (fun (x, y) -> (x,y+2*h)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+2*w,y+2*h)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+2*w,y-2*h)) shape);
  fill_poly (Array.map (fun (x, y) -> (x-2*w,y+2*h)) shape)

(** Draw the player vehicle (the angle is in radians) *)
let draw_player (x, y) theta =
  let shape = [|(20,0);(-20,-10);(-16,0);(-20,10)|] in
  set_color (rgb 0 255 255);
  let realshape = (Array.map
                     (fun (x',y') ->
                        let xf = (float_of_int x')+.x and yf = (float_of_int y')+.y in
                        let x' = (cos theta) *. (xf-.x) -. (sin theta) *. (yf-.y) +. x
                        and y' = (sin theta) *. (xf-.x) +. (cos theta) *. (yf-.y) +. y in
                        (int_of_float x')+w,(int_of_float y')+h)
                     shape) in
  (* We must draw it not only at it's real position, but also if necessary on the other side (tor) *)
  fill_poly_tor realshape

(** Draw the other players *)
let draw_other_players coords =
  set_color red;
  let shape = [|(20,0);(-20,-10);(-16,0);(-20,10)|] in
  List.iter (fun (us, (x, y), _, theta) ->
      if (us = state.player.username) then () else
        let realshape = (Array.map
                     (fun (x',y') ->
                        let xf = (float_of_int x')+.x and yf = (float_of_int y')+.y in
                        let x' = (cos theta) *. (xf-.x) -. (sin theta) *. (yf-.y) +. x
                        and y' = (sin theta) *. (xf-.x) +. (cos theta) *. (yf-.y) +. y in
                        (int_of_float x')+w,(int_of_float y')+h)
                     shape) in
        fill_poly_tor realshape)
    coords

(** Draw the target *)
let draw_target (x, y) =
  set_color green;
  fill_circle ((int_of_float x)+w) ((int_of_float y)+h) 20

(** Draw the obstacles *)
let draw_obstacles coords =
  set_color (rgb 255 102 0);
  List.iter (fun (x, y) -> fill_circle ((int_of_float x)+w) ((int_of_float y)+h) 20) coords

(** Graphic thread;
    `terminator` allows us to gracefully close the client *)
let graph_thread (refresh_tickrate, terminator) =
  start_graph ();
  try
    while true do
      draw_background ();
      if(state.phase = Attente) then draw_loading ()
      else (
        (* Drawing *)
        draw_target state.objCoord;
        draw_obstacles state.obsCoords;
        draw_other_players state.coords;
        draw_player (state.player.coord) (state.player.angle);
      );
      synchronize ();
      Thread.delay (1./.(float_of_int refresh_tickrate))
    done
  (* The graphics window was closed *)
  with Graphic_failure _ -> terminator ()

(** Input thread, handles action from the user;
    `terminator` allows us to gracefully close the client *)
let input_thread terminator =
  Thread.delay 0.1; (* Wait a bit so that the graphic window is already created *)
  try
    while true do
      let c = read_key () in
      if (c = 'w') then thrust ()
      else if (c = 'd') then clock ()
      else if (c = 'a') then anticlock ()
    done
  (* The graphic window was closed **)
  with Graphic_failure _ -> terminator ()
