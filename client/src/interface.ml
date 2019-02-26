(*******************************************************************************)
(*                           Human interface                                   *)
(*   The interface is divided in two parts: the CLI is used to display text    *)
(*              while the game itself is played in a graphic                   *)
(*******************************************************************************)

open Game

(*                       CLI part of the interface                             *)

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
