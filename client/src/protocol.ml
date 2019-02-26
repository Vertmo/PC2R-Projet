(*******************************************************************************)
(*                            Protocol Commands                                *)
(*******************************************************************************)

open Game

(** Coordinates *)
type coord = float * float

let coord_of_string s =
  let strings = String.split_on_char 'Y' s in
  let xs = (String.sub (List.nth strings 0) 1 ((String.length (List.nth strings 0))-1))
  and ys = List.nth strings 1 in
  (float_of_string xs, float_of_string ys)

(** Parses set of coordinates for several players *)
let coords_of_string s =
  let parts = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, coord_of_string (List.nth l 1))) parts

(** Commands from Client to Server *)
type clientcmd =
  (* Part A *)
  | Connect of string
  | Exit of string
  | NewPos of coord

(** Translate a client command to a string, to send it to the server *)
let string_of_clientcmd = function
  | Connect u -> Printf.sprintf "CONNECT/%s/" u
  | Exit u -> Printf.sprintf "EXIT/%s/" u
  | NewPos (x, y) -> Printf.sprintf "NEWPOS/X%fY%f/" x y

(** Liste de scores *)
type scores = (string * int) list

let scores_of_string s =
  let scores = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, int_of_string (List.nth l 1))) scores

(** Commands from Server to Client *)
type servercmd =
  (* Part A *)
  | Welcome of phase * scores * coord
  | Denied
  | NewPlayer of string
  | PlayerLeft of string
  | Session of (string * coord) list * coord
  | Winner of scores
  | Tick of (string * coord) list
  | NewObj of coord * scores

(** Parses a string to a server command *)
let servercmd_of_string s =
  let parts = String.split_on_char '/' s in
  match (List.hd parts) with
  | "WELCOME" -> Welcome (phase_of_string (List.nth parts 1),
                          scores_of_string (List.nth parts 2),
                          coord_of_string (List.nth parts 3))
  | "DENIED" -> Denied
  | "NEWPLAYER" -> NewPlayer (List.nth parts 1)
  | "PLAYERLEFT" -> PlayerLeft (List.nth parts 1)
  | "SESSION" -> Session (coords_of_string (List.nth parts 1),
                          coord_of_string (List.nth parts 2))
  | "WINNER" -> Winner (scores_of_string (List.nth parts 1))
  | "TICK" -> Tick (coords_of_string (List.nth parts 1))
  | "NEWOBJ" -> NewObj ((coord_of_string (List.nth parts 1)),
                        (scores_of_string (List.nth parts 2)))
  | _ -> failwith "Unknown command"

(** Execute a server command. `send` is a function used to send a command back *)
let execute_command send = function
  | Welcome (phase, scores, objCoord) -> (
      Mutex.lock stateMut;
      state.phase <- phase;
      state.scores <- scores;
      state.objCoord <- objCoord;
      Mutex.unlock stateMut;
      Interface.display_welcome ()
    )
  | Denied -> print_endline "The connection was denied"; exit 1
  | NewPlayer u -> print_endline (Printf.sprintf "Player %s just connected" u)
  | PlayerLeft u -> print_endline (Printf.sprintf "Player %s just left" u)
  | Session (coords, objCoord) -> (
      Mutex.lock stateMut;
      state.phase <- Jeu;
      state.coords <- coords;
      List.iter (fun (u, coord) -> if (u = state.player.username) then state.player.coord <- coord) coords;
      state.objCoord <- objCoord;
      Mutex.unlock stateMut;
    )
  | Tick coords -> (
      Mutex.lock stateMut;
      state.coords <- coords;
      Mutex.unlock stateMut;
      send (NewPos state.player.coord) (* Answer *)
    )
  | _ -> failwith "Command execution not yet implemented"
