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
  print_endline "Current Scores :";
  List.iter (fun (n, s) -> Printf.printf "%s: %d\n" n s) s;
  print_endline "==============================="

(** Print the welcome message *)
let display_welcome () =
  print_endline "Welcome to Vector Arena !";
  if(state.phase = Attente) then print_endline "The game is about to start !"
  else print_endline "The game is already on !";
  display_scores state.scores

(*                     Graphic part of the interface                           *)

open Graphics

(** Open the graphic window *)
let start_graph () =
  open_graph (Printf.sprintf " %dx%d" width height);
  auto_synchronize false (* We use double buffering, because we're not monsters *)

(** Draw the game's background *)
let draw_background () =
  clear_graph ();
  set_color black;
  fill_rect 0 0 width height

(** Draw the loading screen *)
let draw_loading _ =
  set_color (rgb 155 155 0);
  draw_segments [|(width/4,2*height/3,3*width/4,2*height/3);
                  (width/4,2*height/3-50,3*width/4,2*height/3-50)|];
  set_text_size 50; (* It unfortunally is not implemented yet... *)
  let (sx, _) = text_size "Vector Arena" in
  moveto (width/2 - sx/2) (2*height/3-30);
  draw_string "Vector Arena"

(** Draw the shape in it's real place, but also on the corresponding borders *)
let fill_poly_tor shape =
  fill_poly shape;
  fill_poly (Array.map (fun (x, y) -> (x-width,y)) shape);
  fill_poly (Array.map (fun (x, y) -> (x,y-height)) shape);
  fill_poly (Array.map (fun (x, y) -> (x-width,y-height)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+width,y)) shape);
  fill_poly (Array.map (fun (x, y) -> (x,y+height)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+width,y+height)) shape);
  fill_poly (Array.map (fun (x, y) -> (x+width,y-height)) shape);
  fill_poly (Array.map (fun (x, y) -> (x-width,y+height)) shape)

(** Draw the player vehicle (the angle is in radians) *)
let draw_player (x, y) theta =
  let shape = [|(20,0);(-20,-10);(-16,0);(-20,10)|] in
  set_color (rgb 0 255 255);
  let realshape = (Array.map
                     (fun (x',y') ->
                        let xf = (float_of_int x')+.x and yf = (float_of_int y')+.y in
                        let x' = (cos theta) *. (xf-.x) -. (sin theta) *. (yf-.y) +. x
                        and y' = (sin theta) *. (xf-.x) +. (cos theta) *. (yf-.y) +. y in
                        (int_of_float x'),(int_of_float y'))
                     shape) in
  (* We must draw it not only at it's real position, but also if necessary on the other side (tor) *)
  fill_poly_tor realshape

(** Draw the other players *)
let draw_other_players coords =
  set_color red;
  List.iter (fun (us, (x, y)) ->
      if (us = state.player.username) then () else
        let shape = [|(0,5);(3,0);(0,-5);(-3,0)|] in
        fill_poly_tor (Array.map (fun (x',y') ->
            ((int_of_float x)+x',(int_of_float y)+y')) shape)) coords

(** Draw the target *)
let draw_target (x, y) =
  set_color green;
  fill_circle (int_of_float x) (int_of_float y) 10

(** Graphic thread *)
let graph_thread refresh_tickrate =
  start_graph ();
  while true do
    draw_background ();
    if(state.phase = Attente) then draw_loading ()
    else (
      (* Drawing *)
      draw_other_players state.coords;
      draw_target state.objCoord;
      draw_player (state.player.coord) (state.player.angle);
    );
    synchronize ();
    Thread.delay (1./.(float_of_int refresh_tickrate))
  done

(** Key presses thread, handles action from the user *)
let key_thread () =
  while true do
    let c = read_key () in
    if (c = 'w') then thrust ()
    else if (c = 'd') then clock ()
    else if (c = 'a') then anticlock ()
  done
